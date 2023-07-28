package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.exception.PnRetryStorageException;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileDownloadApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileMetadataUpdateApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileUploadApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SafeStorageClientImplTest extends BaseTest {

    @Autowired
    SafeStorageClient safeStorageClient;

    @MockBean
    private FileMetadataUpdateApi fileMetadataUpdateApi;
    @MockBean
    private FileUploadApi fileUploadApi;
    @MockBean
    private  FileDownloadApi fileDownloadApi;

    private static final String CONTENT_TYPE = "application/zip";
    private static final String STATUS = "PRELOADED";
    private static final String ATTACHED = "ATTACHED";
    private static final String DOCUMENT_TYPE = "PN_INVOICING_ACTIVITY_REPORT";


    @Test
    public void notifyFileUploaded(){
        Mockito.when(fileMetadataUpdateApi.updateFileMetadata(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(Mono.just(new OperationResultCodeResponseDto()));
        Mono<OperationResultCodeResponseDto> operationResultCodeResponseDtoMono = safeStorageClient.notifyFileUploaded("fileKey");
        Assertions.assertNotNull(operationResultCodeResponseDtoMono);
    }

    @Test
    public void getFileOk(){

        Mockito.when(fileDownloadApi.getFile(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(Mono.just(new FileDownloadResponseDto()));
        Mono<FileDownloadResponseDto> fileDownloadResponseDtoMono = safeStorageClient.getFile("safestorage://");
        Assertions.assertNotNull(fileDownloadResponseDtoMono);
    }

    @Test
    public void getFileKo(){
        FileDownloadResponseDto fileDownloadResponseDto = new FileDownloadResponseDto();
        FileDownloadInfoDto fileDownloadInfoDto = new FileDownloadInfoDto();
        fileDownloadInfoDto.setRetryAfter(BigDecimal.valueOf(10));
        fileDownloadResponseDto.setDownload(fileDownloadInfoDto);
        Mockito.when(fileDownloadApi.getFile(Mockito.any(),Mockito.any(), Mockito.any())).thenReturn(Mono.just(fileDownloadResponseDto));
        StepVerifier.create( safeStorageClient.getFile("safestorage://"))
                .expectErrorMatches(ex -> {
                    assertEquals(PnRetryStorageException.class, ex.getClass());
                    return true;
                })
                .verify();
    }

    @Test
    public void getPresignedUrl(){
        FileCreationRequestDto fileCreationRequestDto = new FileCreationRequestDto();
        fileCreationRequestDto.setContentType(CONTENT_TYPE);
        fileCreationRequestDto.setDocumentType(DOCUMENT_TYPE);
        fileCreationRequestDto.setStatus(STATUS);

        Mockito.when(fileUploadApi.createFile(Mockito.any(), Mockito.any())).thenReturn(Mono.just(new FileCreationResponseDto()));

        Mono<FileCreationResponseDto> fileCreationResponseDtoMono = safeStorageClient.getPresignedUrl();
        assertNotNull(fileCreationResponseDtoMono);
    }

}
