package it.pagopa.pn.platform.config;

import it.pagopa.pn.commons.configs.RuntimeMode;
import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import it.pagopa.pn.commons.configs.aws.AwsServicesClientsConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.client.builder.AwsClientBuilder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.batch.BatchClient;

import java.net.URI;

@Configuration
public class AwsClientConfig extends AwsServicesClientsConfig {
    private final AwsConfigs props;

    public AwsClientConfig(AwsConfigs props) {
        super(props, RuntimeMode.PROD);
        this.props = props;
    }

    @Bean
    public BatchClient awsBatchClient(){
        return this.configureBuilder(BatchClient.builder());
    }

    private <C> C configureBuilder(AwsClientBuilder<?, C> builder) {
        if (this.props != null) {
            String profileName = this.props.getProfileName();
            if (StringUtils.isNotBlank(profileName)) {
                builder.credentialsProvider(ProfileCredentialsProvider.create(profileName));
            }

            String regionCode = this.props.getRegionCode();
            if (StringUtils.isNotBlank(regionCode)) {
                builder.region(Region.of(regionCode));
            }

            String endpointUrl = this.props.getEndpointUrl();
            if (StringUtils.isNotBlank(endpointUrl)) {
                builder.endpointOverride(URI.create(endpointUrl));
            }
        }

        return builder.build();
    }

    
}
