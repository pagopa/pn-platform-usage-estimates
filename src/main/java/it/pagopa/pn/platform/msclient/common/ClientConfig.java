package it.pagopa.pn.platform.msclient.common;

import it.pagopa.pn.commons.pnclients.CommonBaseClient;
import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.api.RecipientsApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.ApiClient;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileDownloadApi;
import it.pagopa.pn.platform.msclient.generated.pnsafestorage.v1.api.FileMetadataUpdateApi;
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

    @Bean
    public FileMetadataUpdateApi getFileMetadataUpdateApi (PnPlatformConfig pnPlatformConfig) {

        ApiClient newApiClient = new ApiClient(super.initWebClient(ApiClient.buildWebClientBuilder()));
        newApiClient.setBasePath(pnPlatformConfig.getClientSafeStorageBasepath());

        return new FileMetadataUpdateApi(newApiClient);
    }

    @Bean
    public RecipientsApi getRecipientsApi(PnPlatformConfig pnPlatformConfig){
        it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.ApiClient apiClient =
                new it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.ApiClient(super.initWebClient(it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(pnPlatformConfig.getClientDataVaultBasepath());
        return new RecipientsApi(apiClient);
    }

}
