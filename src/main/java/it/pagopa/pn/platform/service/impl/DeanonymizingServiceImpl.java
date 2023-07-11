package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.dao.CsvDAO;
import it.pagopa.pn.platform.dao.ZipDAO;
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
    private DataVaultClient dataVaultClient;

    @Override
    public Mono<Void> execute(String paId, String reportKey) {
        return this.activityReportMetaDAO.findByPaIdAndReportKey(paId, reportKey)
                .flatMap(pnActivityReport -> {
                    pnActivityReport.setStatus(ReportStatusEnum.DEANONIMIZING.name());
                    return this.activityReportMetaDAO.createMetaData(pnActivityReport);
                })
                .flatMap(pnActivityReport -> this.getCSV(pnActivityReport.getReportKey())
                            .parallel()
                            .map(this::deanonymizing)
                            .sequential()
                            .collectList()
                            .doOnNext(reportsDeanonymized -> this.csvDAO.write(reportsDeanonymized, FILES_DEANONYMIZED))
                            .doOnNext(reportsDeanonymized -> this.zipDAO.zipFiles(FOLDER_DEANONYMIZED))
                            .map(i -> pnActivityReport)
                )
                .flatMap(report -> this.safeStorageClient.getPresignedUrl()
                            .flatMap(responseSafeStorage -> {
                                report.setKeySafeStorage(responseSafeStorage.getKey());
                                byte[] zipBytes = this.zipDAO.getZipFile(FOLDER_DEANONYMIZED);
                                return this.safeStorageClient.uploadFile(responseSafeStorage.getUploadUrl(), zipBytes)
                                        .then(Mono.just(report));
                            })
                )
                .map(activityReport -> {
                    activityReport.setStatus(ReportStatusEnum.READY.name());
                    return this.activityReportMetaDAO.createMetaData(activityReport);
                })
                .then();
    }

    private Flux<ActivityReportCSV> getCSV(String reportKey) {
        InputStreamReader file = s3Bucket.getObjectData(reportKey);
        return csvDAO.toRows(file);
    }

    private ActivityReportCSV deanonymizing(ActivityReportCSV source){
        if (StringUtils.isNotBlank(source.getRecipientTaxId())){
            source.setRecipientTaxId(dataVaultClient.decode(source.getRecipientTaxId()));
            return source;
        }
        return source;
    }


}
