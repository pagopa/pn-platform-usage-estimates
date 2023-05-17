package it.pagopa.pn.platform.S3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import it.pagopa.pn.platform.config.AwsBucketProperties;
import it.pagopa.pn.platform.config.BaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.File;



class S3BucketImplTest extends BaseTest {
    @MockBean
    AmazonS3 s3Client;

    @Autowired
    @SpyBean
    AwsBucketProperties awsBucketProperties;

    @Autowired
    S3Bucket s3Bucket;

    @Test
    void putObjectOkTest(){
        Mockito.when(this.s3Client.putObject(Mockito.any())).thenReturn(new PutObjectResult());
        this.s3Bucket.putObject("pippo.txt", new File("pippo.txt"));
    }

    @Test
    void putObjectErrorTest(){
        Mockito.when(this.s3Client.putObject(Mockito.any())).thenThrow(new AmazonClientException("ERROR"));
        this.s3Bucket.putObject("pippo.txt", new File("pippo.txt"));

    }

}