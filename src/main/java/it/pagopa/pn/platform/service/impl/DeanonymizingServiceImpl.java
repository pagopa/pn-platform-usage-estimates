package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.dao.CsvDAO;
import it.pagopa.pn.platform.dao.ZipDAO;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.ActivityReportMapper;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.model.ActivityReportCSV;
import it.pagopa.pn.platform.msclient.DataVaultClient;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import it.pagopa.pn.platform.service.DeanonymizingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.time.Duration;


@Service
@Slf4j
public class DeanonymizingServiceImpl implements DeanonymizingService {
    private static final String FOLDER_DEANONYMIZED = "src/main/resources/reports";
    private static final String FILES_DEANONYMIZED = FOLDER_DEANONYMIZED.concat("/report_de_anonymized.csv");

    @Autowired
    private ActivityReportMetaDAO activityReportMetaDAO;
    @Autowired
    private S3Bucket s3Bucket;
    @Autowired
    private CsvDAO csvDAO;
    @Autowired
    private ZipDAO zipDAO;
    @Autowired
    private SafeStorageClient safeStorageClient;
    @Autowired
    private PnPlatformConfig pnPlatformConfig;
    @Autowired
    private DataVaultClient dataVaultClient;

    @Override
    public Mono<Void> execute(String paId, String reportKey) {
        return this.activityReportMetaDAO.findByPaIdAndReportKey(paId, reportKey)
                .flatMap(pnActivityReport -> {
                    pnActivityReport.setStatusReport(ReportStatusEnum.DEANONIMIZING.name());
                    return this.activityReportMetaDAO.createMetaData(pnActivityReport);
                })
                .flatMap(pnActivityReport -> this.getCSV(pnActivityReport.getReportKey())
                            .parallel()
                            .flatMap(this::deanonymizing)
                            .sequential()
                            .collectList()
                            .doOnNext(reportsDeanonymized -> this.csvDAO.write(reportsDeanonymized, FILES_DEANONYMIZED))
                            .doOnNext(reportsDeanonymized -> this.zipDAO.zipFiles(FOLDER_DEANONYMIZED))
                            .onErrorResume(ex -> {
                                ActivityReportMapper.changeReportStatus(pnActivityReport, ReportStatusEnum.ERROR, ex.getMessage());
                                return activityReportMetaDAO.createMetaData(pnActivityReport)
                                        .flatMap(report -> Mono.error(new PnGenericException(ExceptionTypeEnum.DEANONIMIZING_JOB_EXCEPTION, ExceptionTypeEnum.DEANONIMIZING_JOB_EXCEPTION.getMessage())));
                            })
                            .map(i -> pnActivityReport)
                )
                .flatMap(pnActivityReport -> this.safeStorageClient.getPresignedUrl()
                            .flatMap(responseSafeStorage -> {
                                pnActivityReport.setKeySafeStorage(responseSafeStorage.getKey());
                                byte[] zipBytes = this.zipDAO.getZipFile(FOLDER_DEANONYMIZED);
                                return this.safeStorageClient.uploadFile(responseSafeStorage.getUploadUrl(), zipBytes)
                                        .flatMap(item -> safeStorageClient.notifyFileUploaded(responseSafeStorage.getKey()))
                                        .then(Mono.just(pnActivityReport));
                            }).onErrorResume(ex -> {

                                ActivityReportMapper.changeReportStatus(pnActivityReport, ReportStatusEnum.ERROR, ex.getMessage());

                                return activityReportMetaDAO.createMetaData(pnActivityReport)
                                    .flatMap(report -> Mono.error(new PnGenericException(ExceptionTypeEnum.DEANONIMIZING_JOB_EXCEPTION, ExceptionTypeEnum.DEANONIMIZING_JOB_EXCEPTION.getMessage())));

                            })
                )
                .flatMap(activityReport -> {
                    activityReport.setStatusReport(ReportStatusEnum.ENQUEUED.name());
                    return this.activityReportMetaDAO.createMetaData(activityReport);
                })
                .then();
    }

    private Flux<ActivityReportCSV> getCSV(String reportKey) {
        InputStreamReader file = s3Bucket.getObjectData(reportKey);
        return csvDAO.toRows(file);
    }

    private Mono<ActivityReportCSV> deanonymizing(ActivityReportCSV source){
        if (StringUtils.isNotBlank(source.getRecipientTaxId())){
            return getDecodeTaxId(pnPlatformConfig.getAttemptDataVault(), source.getRecipientTaxId(), null)
                    .map(taxId -> {
                        source.setRecipientTaxId(taxId);
                        return source;
                    });
        }
        return Mono.just(source);
    }

    public Mono<String> getDecodeTaxId(Integer n, String taxId, Throwable ex){
        if (n<0)
            return Mono.error(ex);
        else {
            return Mono.delay(Duration.ofMillis( 100L ))
                    .map(item -> dataVaultClient.decode(taxId))
                    .onErrorResume(exception -> {
                        log.error ("Error with retrieve {}", exception.getMessage());
                        return getDecodeTaxId(n - 1, taxId, exception);
                    });
        }
    }

}
