package it.pagopa.pn.platform.config;

import com.amazonaws.services.batch.AWSBatch;
import com.amazonaws.services.batch.AWSBatchClientBuilder;
import it.pagopa.pn.commons.configs.RuntimeMode;
import it.pagopa.pn.commons.configs.aws.AwsConfigs;
import it.pagopa.pn.commons.configs.aws.AwsServicesClientsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsClientConfig extends AwsServicesClientsConfig {


    public AwsClientConfig(AwsConfigs props) {
        super(props, RuntimeMode.PROD);
    }

    @Bean
    public AWSBatch awsBatchClient(){
        return AWSBatchClientBuilder.defaultClient();
    }

}
