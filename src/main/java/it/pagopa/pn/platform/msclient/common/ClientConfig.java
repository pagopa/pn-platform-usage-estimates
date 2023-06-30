package it.pagopa.pn.platform.msclient.common;

import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.ApiClient;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileDownloadApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileUploadApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig  extends CommonBaseClient {
    @Bean
    public FileDownloadApi getSafeStorageClient (PnPlatformConfig pnPlatformConfig){

        ApiClient newApiClient = new ApiClient(super.initWebClient(ApiClient.buildWebClientBuilder()));
        newApiClient.setBasePath(pnPlatformConfig.getClientSafeStorageBasepath());

        return new FileDownloadApi(newApiClient);
    }
    @Bean
    public FileUploadApi getFileUploadAPI (PnPlatformConfig pnPlatformConfig){

        ApiClient newApiClient = new ApiClient(super.initWebClient(ApiClient.buildWebClientBuilder()));
        newApiClient.setBasePath(pnPlatformConfig.getClientSafeStorageBasepath());

        return new FileUploadApi(newApiClient);
    }

}
