package it.pagopa.pn.platform.config;


import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("aws")
@Getter
@Setter
public class AwsPropertiesConfig extends AwsConfigs {
    private String dynamodbEstimateTable;
    private String dynamodbProfilationTable;
    private String dynamodbActivityReportTable;
}
