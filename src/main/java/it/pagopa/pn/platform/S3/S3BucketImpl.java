package it.pagopa.pn.platform.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import it.pagopa.pn.platform.config.AwsBucketProperties;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.File;

@Slf4j
public class S3BucketImpl implements S3Bucket {
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final AmazonS3 s3Client;
    private final AwsBucketProperties awsBucketProperties;

    public S3BucketImpl(AmazonS3 s3Client, AwsBucketProperties awsBucketProperties) {
        this.s3Client = s3Client;
        this.awsBucketProperties = awsBucketProperties;
    }

    @Override
    public Mono<File> putObject(File file) {
        try {
            PutObjectRequest request = new PutObjectRequest(this.awsBucketProperties.getName(), file.getName(), file);
            // set metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.addUserMetadata("title", file.getName());
            metadata.setContentType(JSON_CONTENT_TYPE);
            request.setMetadata(metadata);
            s3Client.putObject(request);
        } catch (Exception e) {
            log.error("Error in upload object in s3 {}", e.getMessage());
        }
        return Mono.just(file);
    }
}
