package it.pagopa.pn.platform.S3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import it.pagopa.pn.platform.config.AwsBucketProperties;
import it.pagopa.pn.platform.config.BaseTest;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;



class S3BucketImplTest extends BaseTest {
    @MockBean
    AmazonS3 s3Client;

    @Autowired
    @SpyBean
    AwsBucketProperties awsBucketProperties;

    @Autowired
    S3Bucket s3Bucket;

    private HttpRequestBase httpRequest = new HttpRequestBase() {
        @Override
        public String getMethod() {
            return null;
        }
    };

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

    @Test
    void getObjectData(){
        S3Object s3Object = new S3Object();
        S3ObjectInputStream s3ObjectInputStream = new S3ObjectInputStream(null,httpRequest);
        s3Object.setObjectContent(s3ObjectInputStream);
        Mockito.when(this.s3Client.getObject(Mockito.any())).thenReturn(s3Object);
        InputStreamReader inputStreamReader = this.s3Bucket.getObjectData("pippo.txt");
        Assertions.assertNotNull(inputStreamReader);
    }

    @Test
    void getObjectDataS3ObjectNull(){
        Mockito.when(this.s3Client.getObject(Mockito.any())).thenReturn(null);
        InputStreamReader inputStreamReader = this.s3Bucket.getObjectData("pippo.txt");
        Assertions.assertNull(inputStreamReader);
    }

    @Test
    void testGetPresignedUrlFile() throws MalformedURLException {
        String expectedBucket = "yourBucket";
        String expectedFileKey = "yourFileKey";
        URL expectedUrl = new URL("https://example.com");

        Mockito.when(s3Client.generatePresignedUrl(Mockito.any(GeneratePresignedUrlRequest.class))).thenReturn(expectedUrl);
        Mono<String> result = s3Bucket.getPresignedUrlFile(expectedBucket, expectedFileKey);

        // Assert the result
        Assertions.assertNotNull(result);
    }
}