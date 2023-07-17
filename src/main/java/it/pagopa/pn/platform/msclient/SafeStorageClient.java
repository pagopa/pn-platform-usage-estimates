package it.pagopa.pn.platform.msclient;


import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileCreationResponseDto;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.FileDownloadResponseDto;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.dto.OperationResultCodeResponseDto;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public interface SafeStorageClient {

    Mono<FileDownloadResponseDto> getFile(String fileKey);
    Mono<FileCreationResponseDto> getPresignedUrl();
    Mono<String> uploadFile(String url, byte[] bytes);
    Mono<OperationResultCodeResponseDto> notifyFileUploaded(String fileKey);
}
