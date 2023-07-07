package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.dao.CsvDAO;
import it.pagopa.pn.platform.dao.DAOException;
import it.pagopa.pn.platform.dao.ZipDAO;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.model.ActivityReportCSV;
import it.pagopa.pn.platform.msclient.DataVaultEncryptionClient;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileCreationResponseDto;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.OperationResultCodeResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import it.pagopa.pn.platform.service.impl.DeanonymizingServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.*;
import java.time.Instant;


import static org.junit.jupiter.api.Assertions.*;

class DeanonymizingServiceTest extends BaseTest{

    @Autowired
    private DeanonymizingServiceImpl deanonymizingServiceImpl;

    @MockBean
    private S3Bucket s3Bucket;

    @MockBean
    private ActivityReportMetaDAO activityReportMetaDAO;

    @MockBean
    private CsvDAO csvDAO;

    @MockBean
    private ZipDAO zipDAO;

    @MockBean
    private DataVaultEncryptionClient dataVaultEncryptionClient;

    @MockBean
    private SafeStorageClient safeStorageClient;


    @Test
    void executeOK() {
        ActivityReportCSV activityReportCSV = getActivityReportCSV();
        PnActivityReport pnActivityReport = getPnActivityReport();
        FileCreationResponseDto creationResponseDto = getFileCreationDTO();
        OperationResultCodeResponseDto operationResultCodeResponseDto = getOperationResult();
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.activityReportMetaDAO.createMetaData(Mockito.any())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.s3Bucket.getObjectData(Mockito.anyString())).thenReturn(new InputStreamReader(InputStream.nullInputStream()));
        Mockito.when(this.csvDAO.toRows(Mockito.any())).thenReturn(Flux.just(activityReportCSV));
        Mockito.when(dataVaultEncryptionClient.decode(Mockito.any())).thenReturn("returnOk");
        Mockito.doNothing().when(this.csvDAO).write(Mockito.any(), Mockito.anyString());
        Mockito.doNothing().when(this.zipDAO).zipFiles(Mockito.any());
        Mockito.when(this.safeStorageClient.getPresignedUrl()).thenReturn(Mono.just(creationResponseDto));
        Mockito.when(this.safeStorageClient.uploadFile(Mockito.anyString(), Mockito.any())).thenReturn(Mono.just("returnOk"));
        Mockito.when(this.safeStorageClient.notifyFileUploaded(Mockito.any())).thenReturn(Mono.just(operationResultCodeResponseDto));
        this.deanonymizingServiceImpl.execute("", "").block();
    }

    @Test
    void executeKOCsvDao(){

        ActivityReportCSV activityReportCSV = getActivityReportCSV();
        PnActivityReport pnActivityReport = getPnActivityReport();
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.activityReportMetaDAO.createMetaData(Mockito.any())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.s3Bucket.getObjectData(Mockito.anyString())).thenReturn(new InputStreamReader(InputStream.nullInputStream()));
        Mockito.when(this.csvDAO.toRows(Mockito.any())).thenReturn(Flux.just(activityReportCSV));
        Mockito.when(dataVaultEncryptionClient.decode(Mockito.any())).thenReturn("returnOk");
        Mockito.doThrow(new DAOException(DAOException.DaoName.CSVDAO, "Error with creating csv file from object")).when(this.csvDAO).write(Mockito.any(), Mockito.anyString());
        Mockito.doNothing().when(this.zipDAO).zipFiles(Mockito.any());
        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.deanonymizingServiceImpl.execute("", "").block();
        });
        assertEquals(ExceptionTypeEnum.DEANONIMIZING_JOB_EXCEPTION, exception.getExceptionType());
    }

    @Test
    void executeKOZipDao(){

        ActivityReportCSV activityReportCSV = getActivityReportCSV();
        PnActivityReport pnActivityReport = getPnActivityReport();
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.activityReportMetaDAO.createMetaData(Mockito.any())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.s3Bucket.getObjectData(Mockito.anyString())).thenReturn(new InputStreamReader(InputStream.nullInputStream()));
        Mockito.when(this.csvDAO.toRows(Mockito.any())).thenReturn(Flux.just(activityReportCSV));
        Mockito.when(dataVaultEncryptionClient.decode(Mockito.any())).thenReturn("returnOk");
        Mockito.doNothing().when(this.csvDAO).write(Mockito.any(), Mockito.anyString());
        Mockito.doThrow(new DAOException(DAOException.DaoName.ZIPDAO, "The folder of the reports doesn't exist")).when(this.zipDAO).zipFiles(Mockito.any());
        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.deanonymizingServiceImpl.execute("", "").block();
        });
        assertEquals(ExceptionTypeEnum.DEANONIMIZING_JOB_EXCEPTION, exception.getExceptionType());
    }

    @Test
    void executeKOGetZipFile(){
        ActivityReportCSV activityReportCSV = getActivityReportCSV();
        PnActivityReport pnActivityReport = getPnActivityReport();
        FileCreationResponseDto creationResponseDto = getFileCreationDTO();
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.activityReportMetaDAO.createMetaData(Mockito.any())).thenReturn(Mono.just(pnActivityReport));
        Mockito.when(this.s3Bucket.getObjectData(Mockito.anyString())).thenReturn(new InputStreamReader(InputStream.nullInputStream()));
        Mockito.when(this.csvDAO.toRows(Mockito.any())).thenReturn(Flux.just(activityReportCSV));
        Mockito.when(dataVaultEncryptionClient.decode(Mockito.any())).thenReturn("returnOk");
        Mockito.doNothing().when(this.csvDAO).write(Mockito.any(), Mockito.anyString());
        Mockito.doNothing().when(this.zipDAO).zipFiles(Mockito.any());
        Mockito.when(this.safeStorageClient.getPresignedUrl()).thenReturn(Mono.just(creationResponseDto));
        Mockito.doThrow(new DAOException(DAOException.DaoName.ZIPDAO, "The folder of the reports doesn't exist")).when(this.zipDAO).getZipFile(Mockito.any());
        PnGenericException exception = assertThrows(PnGenericException.class, ()-> {
            this.deanonymizingServiceImpl.execute("", "").block();
        });
        assertEquals(ExceptionTypeEnum.DEANONIMIZING_JOB_EXCEPTION, exception.getExceptionType());
    }


    private ActivityReportCSV getActivityReportCSV() {
        ActivityReportCSV activityReportCSV = new ActivityReportCSV();
        activityReportCSV.setRecipientTaxId("ABCFED89B324E791U");
        return activityReportCSV;
    }

    private PnActivityReport getPnActivityReport(){
        PnActivityReport activityReport = new PnActivityReport();
        activityReport.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        activityReport.setBucketName("/report_attivita_pn_from_datalake/cc1c6a8e-5967-42c6-9d83-bfb12ba1665a/<YYYY-MM>/<timestampGenerazioneReport>/report-notificazioni-perfezionate-part-1.csv");
        activityReport.setReportKey("abc1234");
        activityReport.setGenerationDate(Instant.now());
        activityReport.setStatusReport(ReportStatusEnum.RAW.getValue());
        activityReport.setPart("1");
        return activityReport;

    }

    private FileCreationResponseDto getFileCreationDTO(){
        FileCreationResponseDto dto = new FileCreationResponseDto();
        dto.setKey("safeStoragekey");
        dto.setUploadUrl("httt://safestorage.com/downloadFile");
        return dto;
    }

    private OperationResultCodeResponseDto getOperationResult() {
        OperationResultCodeResponseDto dto = new OperationResultCodeResponseDto();
        dto.setResultCode("ok");
        dto.setResultDescription("fileCaricato");
        return dto;
    }
}
