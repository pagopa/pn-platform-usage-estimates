package it.pagopa.pn.platform.service;

import com.amazonaws.services.batch.AWSBatch;
import com.amazonaws.services.batch.model.SubmitJobResult;
import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.service.impl.AwsBatchServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

public class AwsBatchServiceImplTest extends BaseTest {

    @MockBean
    private AWSBatch awsBatch;
    @Autowired
    private PnPlatformConfig pnPlatformConfig;
    @Autowired
    private AwsBatchServiceImpl awsBatchService;

    private SubmitJobResult submitJobResult = new SubmitJobResult();

    @BeforeEach
    public void init(){
        submitJobResult.setJobArn("JobArn");
        submitJobResult.setJobId("jobId");
        submitJobResult.setJobName("jobName");
    }



    @Test
    public void scheduleJob(){

        Mockito.when(this.awsBatch.submitJob(Mockito.any())).thenReturn(submitJobResult);

        String result = awsBatchService.scheduleJob("paId", "bucket", "reportKey");

        // Perform assertions on the result
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

}
