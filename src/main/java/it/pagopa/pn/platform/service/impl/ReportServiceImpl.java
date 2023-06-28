package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.FileMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.service.AwsBatchService;
import it.pagopa.pn.platform.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.*;
import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.STATUS_NOT_IN_ERROR;

@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;

    @Autowired
    private S3Bucket s3Bucket;

    @Autowired
    private SafeStorageClient safeStorageClient;

    @Autowired
    private AwsBatchService awsBatchService;

    @Override
    public Mono<InfoDownloadDTO> downloadReportFile(String paId, String reportKey, String type) {

        return this.activityReportMetaDAO.findByPaIdAndReportKey(paId, reportKey)
                .switchIfEmpty(Mono.error(new PnGenericException(REPORT_NOT_EXISTS, REPORT_NOT_EXISTS.getMessage())))
                .flatMap(pnActivityReport -> {
                    if (!type.equals("SOURCE") && !type.equals("TARGET")) return Mono.error(new PnGenericException(BAD_REQUEST, BAD_REQUEST.getMessage()));

                    if (type.equals("SOURCE")){
                        return this.s3Bucket.getPresignedUrlFile(pnActivityReport.getReportKey(), pnActivityReport.getBucketName())
                                .switchIfEmpty(Mono.error(new PnGenericException(FILE_KEY_NOT_EXISTED, FILE_KEY_NOT_EXISTED.getMessage())))
                                .map(url -> FileMapper.toDownloadFile(pnActivityReport, url));
                    }

                    if(!pnActivityReport.getStatus().equals(String.valueOf(InfoDownloadDTO.StatusEnum.READY))) {
                        return Mono.error(new PnGenericException(STATUS_NOT_READY, STATUS_NOT_READY.getMessage()));
                    }

                    return this.safeStorageClient.getFile(pnActivityReport.getReportZipKey())
                                .switchIfEmpty(Mono.error(new PnGenericException(FILE_KEY_NOT_EXISTED, FILE_KEY_NOT_EXISTED.getMessage())))
                                .map(file -> {
                                    if (file.getDownload() != null) {
                                        return FileMapper.toDownloadFile(pnActivityReport, file.getDownload().getUrl());
                                    }
                                    return FileMapper.toDownloadFile(pnActivityReport, "");
                                });


                });

    }

    @Override
    public Mono<PageableDeanonymizedFilesResponseDto> getAllDeanonymizedFiles(String paId, String status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);

        //caso in cui non mi viene passato lo status -> mostro tutti i record
        if (StringUtils.isBlank(status)){
            return activityReportMetaDAO.findAllFromPaId(paId)
                    .collectList()
                    .map(list ->
                            FileMapper.toPagination(pageable, list)
                    )
                    .map(FileMapper::toPageableResponse);
        }

        //caso in cui mi viene passato uno dei 4 stati previsti
        else if (checkStatusReport(status)){
            return activityReportMetaDAO.findAllFromPaIdAndStatus(paId, status)
                    .filter(activityReport -> activityReport.getStatus().equals(status))
                    .collectList()
                    .map(list ->
                            FileMapper.toPagination(pageable, list)
                    )
                    .map(FileMapper::toPageableResponse);

        }
        //mi viene passato uno stato diverso da quei 4
        return Mono.error(new PnGenericException(STATUS_NOT_CORRECT, STATUS_NOT_CORRECT.getMessage()));
    }

    @Override
    public Flux<InfoDownloadDTO> getAllReportFile(String paId, String referenceMonth) {
        return this.activityReportMetaDAO.findAllFromPaId(paId, referenceMonth)
                .filter(pnActivityReport ->  pnActivityReport.getStatus().equals(String.valueOf(InfoDownloadDTO.StatusEnum.READY)))
                .map(pnActivityReport -> FileMapper.fromPnActivityReportToInfoDownloadDTO(paId, referenceMonth, pnActivityReport));
    }

    @Override
    public Mono<Void> getScheduleDeanonymizedFiles(String paId, String reportKey) {
        return this.activityReportMetaDAO.findByPaIdAndReportKey(paId, reportKey)
                .switchIfEmpty(Mono.error(new PnGenericException(REPORT_NOT_EXISTS, REPORT_NOT_EXISTS.getMessage())))
                .doOnNext(activityReport -> {
                    if (!activityReport.getStatus().equals(String.valueOf(InfoDownloadDTO.StatusEnum.ERROR))){
                        throw new PnGenericException(STATUS_NOT_IN_ERROR, STATUS_NOT_IN_ERROR.getMessage());
                    }
                    this.awsBatchService.scheduleJob(paId, activityReport.getBucketName(), reportKey);
                }).then();
    }

    private Boolean checkStatusReport(String status){
        return status.equalsIgnoreCase(String.valueOf(InfoDownloadDTO.StatusEnum.READY)) ||
                status.equalsIgnoreCase(String.valueOf(InfoDownloadDTO.StatusEnum.ENQUEUED)) ||
                status.equalsIgnoreCase(String.valueOf(InfoDownloadDTO.StatusEnum.ERROR)) ||
                status.equalsIgnoreCase(String.valueOf(InfoDownloadDTO.StatusEnum.DEANONIMIZING));
    }
}
