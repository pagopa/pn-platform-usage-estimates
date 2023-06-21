package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.api.InfoPaApi;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import org.joda.time.field.OffsetDateTimeField;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;


class ExternalRegistriesClientImplTest extends BaseTest.WithMockServer {
    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;

    @Test
    void externalRegistriesClient200RequestTest() {
        PaInfoDto paInfo = externalRegistriesClient.getOnePa("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a").block();
        Assertions.assertNotNull(paInfo);
        Assertions.assertEquals("cc1c6a8e-5967-42c6-9d83-bfb12ba1665a", paInfo.getId());
    }

    @Test
    void externalRegistriesClient400Test() {
        externalRegistriesClient.getOnePa("cc1c6a8e-5967-42c6-9d83-bad-400")
                .onErrorResume(WebClientResponseException.class, ex -> {
                    Assertions.assertEquals(ex.getStatusCode(), HttpStatus.BAD_REQUEST);
                    return Mono.empty();
                }).block();
    }

    @Test
    void externalRegistriesClient500Test(){
        externalRegistriesClient.getOnePa("cc1c6a8e-5967-42c6-9d83-bad-500")
                .onErrorResume(WebClientResponseException.class, ex -> {
                    Assertions.assertEquals(ex.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return Mono.empty();
                }).block();
    }

}