package it.pagopa.pn.platform;

import it.pagopa.pn.platform.config.PnPlatformConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnNotWebApplication
public class EstimateUsageBatchApplication implements CommandLineRunner {

    private final PnPlatformConfig pnPlatformConfig;

    public EstimateUsageBatchApplication(PnPlatformConfig pnPlatformConfig) {
        this.pnPlatformConfig = pnPlatformConfig;
    }

    @Override
    public void run(String... args) {

        String datalakeBucketName = pnPlatformConfig.getDatalakeBucketName();
        String datalakeReportKey = pnPlatformConfig.getDatalakeReportKey();

        log.info("Running batch with params bucketName = {}, reportKey = {}", datalakeBucketName, datalakeReportKey);
    }
}
