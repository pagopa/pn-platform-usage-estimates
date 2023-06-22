package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.service.AwsBatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.batch.BatchClient;
import software.amazon.awssdk.services.batch.model.SubmitJobRequest;
import software.amazon.awssdk.services.batch.model.SubmitJobResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AwsBatchServiceImpl implements AwsBatchService {
    @Autowired
    private BatchClient awsBatch;
    @Autowired
    private PnPlatformConfig pnPlatformConfig;

    @Override
    public String scheduleJob(String paId, String bucket, String reportKey) {
        log.info("Schedule a new JOB for {} paId and {} reportKey", paId, reportKey);
        Map<String, String> map = new HashMap<>();
        map.put("PA_ID", paId);
        map.put("PN_PLATFORMUSAGEESTIMATES_DATALAKEBUCKETNAME", bucket);
        map.put("PN_PLATFORMUSAGEESTIMATES_DATALAKEREPORTKEY", reportKey);

        log.debug("Job Queue : {}", pnPlatformConfig.getJobQueueName());
        log.debug("Job Definition : {}", pnPlatformConfig.getJobDefinitionName());

        SubmitJobRequest jobRequest = SubmitJobRequest.builder()
                .jobName(UUID.randomUUID().toString())
                .jobQueue(pnPlatformConfig.getJobQueueName())
                .jobDefinition(pnPlatformConfig.getJobDefinitionName())
                .parameters(map)
                .build();
        
        SubmitJobResponse result = awsBatch.submitJob(jobRequest);
        return result.jobId();
    }
}
