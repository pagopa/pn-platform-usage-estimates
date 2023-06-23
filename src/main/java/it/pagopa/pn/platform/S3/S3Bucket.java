package it.pagopa.pn.platform.S3;

import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStreamReader;

public interface S3Bucket {

    Mono<File> putObject(String filePath, File file);
    InputStreamReader getObjectData(String fileKey);

    Mono<String> getPresignedUrlFile(String bucket, String fileKey);

}
