package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.exception.PnRetryStorageException;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileDownloadApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileDownloadResponseDto;
import it.pagopa.pn.platform.msclient.SafeStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;


@Component
@Slf4j
public class SafeStorageClientImpl implements SafeStorageClient {
    private PnPlatformConfig pnPlatformConfig;
    private FileDownloadApi fileDownloadApi;

    public SafeStorageClientImpl(PnPlatformConfig cfg,
                                 FileDownloadApi fileDownloadApi) {
        this.pnPlatformConfig = cfg;
        this.fileDownloadApi = fileDownloadApi;
    }


    @Override
    public Mono<FileDownloadResponseDto> getFile(String fileKey) {
        String reqFileKey = fileKey;
        log.info("Getting file with {} key", fileKey);
        String BASE_URL = "safestorage://";
        if (fileKey.contains(BASE_URL)){
            fileKey = fileKey.replace(BASE_URL, "");
        }
        log.debug("Req params : {}", fileKey);

        return fileDownloadApi.getFile(fileKey, this.pnPlatformConfig.getSafeStorageCxId(), false)
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(500))
                                .filter(throwable -> throwable instanceof TimeoutException || throwable instanceof ConnectException)
                )
                .map(response -> {
                    if(response.getDownload() != null && response.getDownload().getRetryAfter() != null) {
                        throw new PnRetryStorageException(response.getDownload().getRetryAfter());
                    }
                    response.setKey(reqFileKey);
                    return response;
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error(ex.getResponseBodyAsString());
                    return Mono.error(ex);
                });
    }
}
