package it.pagopa.pn.platform.service.impl;

import com.amazonaws.services.batch.AWSBatch;
import com.amazonaws.services.batch.model.SubmitJobRequest;
import com.amazonaws.services.batch.model.SubmitJobResult;
import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.service.AwsBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AwsBatchServiceImpl implements AwsBatchService {
    @Autowired
    private AWSBatch awsBatch;
    @Autowired
    private PnPlatformConfig pnPlatformConfig;

    @Override
    public String scheduleJob(String paId, String bucket, String reportKey) {
        log.info("Schedule a new JOB for {} paId and {} fileKey", paId, reportKey);
        Map<String, String> map = new HashMap<>();
        map.put("PA_ID", paId);
        map.put("PN_PLATFORMUSAGEESTIMATES_DATALAKEBUCKETNAME", bucket);
        map.put("PN_PLATFORMUSAGEESTIMATES_DATALAKEREPORTKEY", reportKey);

        log.debug("Job Queue : {}", pnPlatformConfig.getJobQueueName());
        log.debug("Job Definition : {}", pnPlatformConfig.getJobDefinitionName());

        SubmitJobRequest jobRequest = new SubmitJobRequest()
                .withJobName(UUID.randomUUID().toString())
                .withJobQueue(pnPlatformConfig.getJobQueueName())
                .withJobDefinition(pnPlatformConfig.getJobDefinitionName())
                .withParameters(map);


        SubmitJobResult result = awsBatch.submitJob(jobRequest);
        log.info("Result of submit job {}", result);
        return result.getJobId();
    }
}
