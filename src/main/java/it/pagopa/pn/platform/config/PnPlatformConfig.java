package it.pagopa.pn.platform.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "pn.platform-usage-estimates")
@Import(SharedAutoConfiguration.class)
public class PnPlatformConfig {
    private String clientExternalRegistriesBasepath;
    private String xPagopaExtchCxId;
    private String safeStorageCxId;
    private String clientDataVaultBasepath;

    private String datalakeBucketName;
    private String datalakeReportKey;

    private String jobQueueName;
    private String jobDefinitionName;
}
