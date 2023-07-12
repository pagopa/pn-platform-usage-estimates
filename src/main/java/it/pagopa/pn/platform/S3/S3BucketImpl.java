package it.pagopa.pn.platform.S3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import it.pagopa.pn.platform.config.AwsBucketProperties;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Instant;

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
    public Mono<File> putObject(String filePath, File file) {
        try {
            PutObjectRequest request = new PutObjectRequest(this.awsBucketProperties.getName(), filePath.concat(file.getName()), file);
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

    @Override
    public InputStreamReader getObjectData(String fileKey) {
        S3Object fullObject = s3Client.getObject(new GetObjectRequest(this.awsBucketProperties.getName(), fileKey));
        InputStreamReader data = null;
        if (fullObject != null) {
            data = new InputStreamReader(fullObject.getObjectContent());
        }
        return data;
    }

    @Override
    public Mono<String> getPresignedUrlFile(String bucket, String fileKey) {
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = Instant.now().toEpochMilli();
        expTimeMillis += 100000 * 60 * 60;
        expiration.setTime(expTimeMillis);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                this.awsBucketProperties.getName(),
                fileKey,
                HttpMethod.GET
        );
        request.setExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(request);
        return Mono.just(url.toExternalForm());
    }
}
