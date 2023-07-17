package it.pagopa.pn.platform.S3;

import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public interface S3Bucket {

    void putObject(String filePath, String fileName, InputStream stream);
    InputStreamReader getObjectData(String fileKey);
    Mono<String> getPresignedUrlFile(String bucket, String fileKey);
}
