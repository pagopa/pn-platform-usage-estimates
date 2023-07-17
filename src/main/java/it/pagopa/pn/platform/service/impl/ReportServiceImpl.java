package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.FileMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.ReportDTO;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import it.pagopa.pn.platform.service.AwsBatchService;
import it.pagopa.pn.platform.service.ReportService;
import lombok.extern.slf4j.Slf4j;
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
    public Mono<ReportDTO> downloadReportFile(String paId, String reportKey, String type) {
        return this.activityReportMetaDAO.findByPaIdAndReportKey(paId, reportKey)
                .switchIfEmpty(Mono.error(new PnGenericException(REPORT_NOT_EXISTS, REPORT_NOT_EXISTS.getMessage())))
                .flatMap(pnActivityReport -> {
                    if (!type.equals("SOURCE") && !type.equals("TARGET"))
                        return Mono.error(new PnGenericException(BAD_REQUEST, BAD_REQUEST.getMessage()));

                    if (type.equals("SOURCE")){
                        return this.s3Bucket.getPresignedUrlFile(pnActivityReport.getBucketName(), pnActivityReport.getReportKey())
                                .switchIfEmpty(Mono.error(new PnGenericException(FILE_KEY_NOT_EXISTED, FILE_KEY_NOT_EXISTED.getMessage())))
                                .map(url -> FileMapper.toDownloadFile(pnActivityReport, url));
                    }

                    if(!pnActivityReport.getStatusReport().equals(String.valueOf(ReportStatusEnum.READY))) {
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
    public Mono<PageableDeanonymizedFilesResponseDto> getAllDeanonymizedFiles(String paId, ReportStatusEnum status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);

        Flux<PnActivityReport> allReportsFromDB = activityReportMetaDAO.findAllFromPaId(paId);
        if (status != null) {
            allReportsFromDB = activityReportMetaDAO.findAllFromPaIdAndStatus(paId, status.getValue());
        }

        return allReportsFromDB
                .collectList()
                .map(list -> FileMapper.toPagination(pageable, list))
                .map(FileMapper::toPageableResponse);
    }

    @Override
    public Flux<ReportDTO> getAllReportFile(String paId, String referenceMonth) {
        return this.activityReportMetaDAO.findAllFromPaId(paId, referenceMonth, ReportStatusEnum.READY.getValue())
                .map(pnActivityReport -> FileMapper.fromPnActivityReportToInfoDownloadDTO(paId, referenceMonth, pnActivityReport));
    }

    @Override
    public Mono<Void> getScheduleDeanonymizedFiles(String paId, String reportKey) {
        return this.activityReportMetaDAO.findByPaIdAndReportKey(paId, reportKey)
                .switchIfEmpty(Mono.error(new PnGenericException(REPORT_NOT_EXISTS, REPORT_NOT_EXISTS.getMessage())))
                .doOnNext(activityReport -> {
                    if (!activityReport.getStatusReport().equals(String.valueOf(ReportStatusEnum.ERROR))){
                        throw new PnGenericException(STATUS_NOT_IN_ERROR, STATUS_NOT_IN_ERROR.getMessage());
                    }
                    this.awsBatchService.scheduleJob(paId, activityReport.getBucketName(), reportKey);
                })
                .then();
    }
}
