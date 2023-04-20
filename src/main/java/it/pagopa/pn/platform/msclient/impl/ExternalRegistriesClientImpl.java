package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.msclient.common.BaseClient;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.ApiClient;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.api.InfoPaApi;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.rest.v1.dto.EstimateCreateBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class ExternalRegistriesClientImpl extends BaseClient implements ExternalRegistriesClient {
    private final PnPlatformConfig pnPlatformConfig;
    private InfoPaApi infoPaApi;

    public ExternalRegistriesClientImpl(PnPlatformConfig pnPlatformConfig) {
        this.pnPlatformConfig = pnPlatformConfig;

    }
    @PostConstruct
    public void init(){
        ApiClient newApiClient = new ApiClient(super.initWebClient(ApiClient.buildWebClientBuilder()));
        newApiClient.setBasePath(this.pnPlatformConfig.getClientExternalRegistriesBasepath());
        this.infoPaApi = new InfoPaApi(newApiClient);
    }

    @Override
    public Mono<PaInfoDto> getOnePa(String id) {
        log.debug("Retrieve detail PA from external registries with id {}", id);
        return this.infoPaApi.getOnePa(this.pnPlatformConfig.getXPagopaExtchCxId())
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(500))
                                .filter(throwable -> throwable instanceof TimeoutException || throwable instanceof ConnectException)

                )
                .map(paInfo -> {
                        log.debug("PaInfo : {}", paInfo);
                        return paInfo;
                        })
                .onErrorResume(ex -> {
                    log.error("Error with retrieve PA detail {} - message {}", id, ex.getMessage(), ex);
                    return Mono.error(ex);
                });
    }
}
