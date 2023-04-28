package it.pagopa.pn.platform.S3;

import reactor.core.publisher.Mono;

import java.io.File;

public interface S3Bucket {

    Mono<File> putObject(String filePath, File file);
}
