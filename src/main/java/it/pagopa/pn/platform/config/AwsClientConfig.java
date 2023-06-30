package it.pagopa.pn.platform.config;

import com.amazonaws.services.batch.AWSBatch;
import com.amazonaws.services.batch.AWSBatchClientBuilder;
import it.pagopa.pn.commons.configs.RuntimeMode;
import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import it.pagopa.pn.commons.configs.aws.AwsServicesClientsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class AwsClientConfig extends AwsServicesClientsConfig {
    private final AwsConfigs props;

    public AwsClientConfig(AwsConfigs props) {
        super(props, RuntimeMode.PROD);
        this.props = props;
    }

    @Bean
    public AWSBatch awsBatchClient(){
        final AWSBatchClientBuilder builder = AWSBatchClientBuilder.standard();
        Optional.ofNullable(props.getRegionCode()).ifPresent(builder::setRegion);
        return builder.build();
    }
    
}
