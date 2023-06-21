package it.pagopa.pn.platform.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import it.pagopa.pn.platform.config.AwsBucketProperties;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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

    public InputStreamReader getObjectData(String fileKey) {
        S3Object fullObject = s3Client.getObject(new GetObjectRequest(this.awsBucketProperties.getName(), fileKey));
        InputStreamReader data = null;
        if (fullObject != null) {
            data = new InputStreamReader(fullObject.getObjectContent());
        }
        return data;
    }
}
