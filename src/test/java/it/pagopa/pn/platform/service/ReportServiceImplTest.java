package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.S3.S3Bucket;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileDownloadInfoDto;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileDownloadResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.ReportDTO;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import it.pagopa.pn.platform.service.impl.ReportServiceImpl;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReportServiceImplTest extends BaseTest {

    @Autowired
    private ReportServiceImpl reportServiceImpl;

    @MockBean
    private ActivityReportMetaDAO activityReportMetaDAO;

    @MockBean
    private SafeStorageClient safeStorageClient;

    @MockBean
    private S3Bucket s3Bucket;

    @MockBean
    private AwsBatchService awsBatchService;

    private PnActivityReport pnActivityReport1 = new PnActivityReport();
    private PnActivityReport pnActivityReport2 = new PnActivityReport();


    @BeforeEach
    public void setUp(){
        initialValue();
    }

    private void initialValue(){
        pnActivityReport1.setPaId("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a");
        pnActivityReport1.setReportKey("DICEMBRE-03");
        pnActivityReport1.setStatusReport("DOWNLOADED");
        pnActivityReport1.setReferenceMonth("DIC-2022");
        pnActivityReport1.setReportZipKey("reportZipKey");
        pnActivityReport1.setBucketName("BucketName");
        pnActivityReport1.setLastModifiedDate(Instant.now());
        pnActivityReport1.setErrorMessage("Error message");

        this.activityReportMetaDAO.createMetaData(pnActivityReport1);

    }

    @Test
    public void downloadReportFileok(){

        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.just("string"));
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        Mono<ReportDTO> reportDTO = this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","type");
        assertNotNull(reportDTO);
    }

    @Test
    public void downloadReportFileko(){

        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.just("string"));
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        StepVerifier.create(this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","type"))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.REPORT_NOT_EXISTS, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    public void downloadReportFile(){

        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.just("string"));
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        StepVerifier.create(this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","type"))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.BAD_REQUEST, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    public void downloadReportFileTypeSource(){

        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        StepVerifier.create(this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","SOURCE"))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.FILE_KEY_NOT_EXISTED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    public void downloadReportFileTypeSourceNotNull(){

        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        Mono<ReportDTO> reportDTOMono = this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","SOURCE");
        assertNotNull(reportDTOMono);
    }

    @Test
    public void downloadReportFileTypeTarget(){

        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        StepVerifier.create(this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","TARGET"))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.STATUS_NOT_READY, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    public void downloadReportFilesetReportKeyReady(){

        pnActivityReport1.setStatusReport(  "READY");
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.empty());
        StepVerifier.create(this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","TARGET"))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.FILE_KEY_NOT_EXISTED, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    public void downloadReportFileGetFile(){

        FileDownloadResponseDto fileDownloadResponseDto =  new FileDownloadResponseDto();
        FileDownloadInfoDto fileDownloadInfoDto = new FileDownloadInfoDto();
        fileDownloadInfoDto.setUrl("url");
        fileDownloadResponseDto.setDownload(fileDownloadInfoDto);
        pnActivityReport1.setStatusReport(  "READY");
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(fileDownloadResponseDto));
        Mono<ReportDTO> reportDTO = this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","TARGET");
        assertNotNull(reportDTO);
    }

    @Test
    public void downloadReportFileGetFileDownlodNull(){

        FileDownloadResponseDto fileDownloadResponseDto =  new FileDownloadResponseDto();
        pnActivityReport1.setStatusReport(  "READY");
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.s3Bucket.getPresignedUrlFile(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        Mockito.when(this.safeStorageClient.getFile(Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        Mono<ReportDTO> reportDTO = this.reportServiceImpl.downloadReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", "reoprtKey","TARGET");
        assertNotNull(reportDTO);
    }

    @Test
    public void getAllDeanonymizedFiles(){

        Mockito.when(this.activityReportMetaDAO.findAllFromPaId(Mockito.any())).thenReturn(Flux.just(pnActivityReport1));
        Mono<PageableDeanonymizedFilesResponseDto> pageableDeanonymizedFilesResponseDtoMono = this.reportServiceImpl.getAllDeanonymizedFiles("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", null,1,1);
        assertNotNull(pageableDeanonymizedFilesResponseDtoMono);
    }

    @Test
    public void getAllDeanonymizedFilesTwo(){

        Mockito.when(this.activityReportMetaDAO.findAllFromPaIdAndStatus(Mockito.any(),Mockito.any())).thenReturn(Flux.just(pnActivityReport1));
        Mono<PageableDeanonymizedFilesResponseDto> pageableDeanonymizedFilesResponseDtoMono = this.reportServiceImpl.getAllDeanonymizedFiles("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", ReportStatusEnum.READY,1,1);
        assertNotNull(pageableDeanonymizedFilesResponseDtoMono);
    }

    @Test
    public void getAllReportFile(){

        Mockito.when(this.activityReportMetaDAO.findAllFromPaId(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(Flux.just(pnActivityReport1));
        Flux<ReportDTO> reportDTOFlux = this.reportServiceImpl.getAllReportFile("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a",pnActivityReport1.getReferenceMonth());
        assertNotNull(reportDTOFlux);
    }

    @Test
    public void getScheduleDeanonymizedFiles(){

        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.empty());
        StepVerifier.create( this.reportServiceImpl.getScheduleDeanonymizedFiles("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a",pnActivityReport1.getReferenceMonth()))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.REPORT_NOT_EXISTS, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    public void getScheduleDeanonymizedFilesNotError(){


        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        StepVerifier.create( this.reportServiceImpl.getScheduleDeanonymizedFiles("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a",pnActivityReport1.getReferenceMonth()))
                .expectErrorMatches(ex -> {
                    assertEquals(PnGenericException.class, ex.getClass());
                    assertEquals(ExceptionTypeEnum.STATUS_NOT_IN_ERROR, ((PnGenericException) ex).getExceptionType());
                    return true;
                })
                .verify();
    }

    @Test
    public void getScheduleDeanonymizedFilesError(){

        pnActivityReport1.setStatusReport(String.valueOf(ReportStatusEnum.ERROR));
        Mockito.when(this.activityReportMetaDAO.findByPaIdAndReportKey(Mockito.any(),Mockito.any())).thenReturn(Mono.just(pnActivityReport1));
        Mockito.when(this.awsBatchService.scheduleJob(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn("string");
        this.reportServiceImpl.getScheduleDeanonymizedFiles("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a",pnActivityReport1.getReferenceMonth());
    }





}
