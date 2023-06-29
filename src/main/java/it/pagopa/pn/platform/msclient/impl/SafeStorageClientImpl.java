package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.exception.PnRetryStorageException;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileDownloadApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileUploadApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileCreationRequestDto;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileCreationResponseDto;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileDownloadResponseDto;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;


@Component
@Slf4j
public class SafeStorageClientImpl implements SafeStorageClient {
    private final PnPlatformConfig pnPlatformConfig;
    private final FileDownloadApi fileDownloadApi;
    private final FileUploadApi fileUploadApi;

    private static final String DOCUMENT_TYPE = "PN_INVOICING_ACTIVITY_REPORT";

    private static final String CONTENT_TYPE = "application/zip";
    private static final String STATUS = "PRELOADED";

    public SafeStorageClientImpl(PnPlatformConfig cfg,
                                 FileDownloadApi fileDownloadApi,
                                 FileUploadApi fileUploadApi) {
        this.pnPlatformConfig = cfg;
        this.fileDownloadApi = fileDownloadApi;
        this.fileUploadApi = fileUploadApi;
    }


    @Override
    public Mono<FileDownloadResponseDto> getFile(String fileKey) {
        log.info("Getting file with {} key", fileKey);
        final String BASE_URL = "safestorage://";
        if (fileKey.contains(BASE_URL)){
            fileKey = fileKey.replace(BASE_URL, "");
        }
        log.debug("Req params : {}", fileKey);

        String finalFileKey = fileKey;
        return fileDownloadApi.getFile(fileKey, this.pnPlatformConfig.getSafeStorageCxId(), false)
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(500))
                                .filter(throwable -> throwable instanceof TimeoutException || throwable instanceof ConnectException)
                )
                .flatMap(response -> {
                    if(response.getDownload() != null && response.getDownload().getRetryAfter() != null) {
                        return Mono.error(new PnRetryStorageException(response.getDownload().getRetryAfter()));
                    }
                    response.setKey(finalFileKey);
                    return Mono.just(response);
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error(ex.getResponseBodyAsString());
                    return Mono.error(ex);
                });
    }

    @Override
    public Mono<FileCreationResponseDto> getPresignedUrl() {
        FileCreationRequestDto fileCreationRequestDto = new FileCreationRequestDto();
        fileCreationRequestDto.setContentType(CONTENT_TYPE);
        fileCreationRequestDto.setDocumentType(DOCUMENT_TYPE);
        fileCreationRequestDto.setStatus(STATUS);

        return fileUploadApi.createFile(this.pnPlatformConfig.getSafeStorageCxId(), fileCreationRequestDto);
    }

    @Override
    public Mono<String> uploadFile(String url, byte [] zipFile) {
        try {
            var dataFactory = new DefaultDataBufferFactory();
            var dataBuffer = dataFactory.allocateBuffer(zipFile.length);
            dataBuffer.write(zipFile);

            return WebClient.create()
                    .put()
                    .uri(new URI(url))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(Flux.just(zipFile), byte[].class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(Mono::just);
        } catch (URISyntaxException e) {
            return Mono.error(e);
        }
    }
}
