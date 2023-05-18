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

    private final PaInfoDto pa = new PaInfoDto();

    @BeforeEach
    void setUp(){
        initialize();
    }

    //@Test
    void testOK() {
        //externalRegistriesClient.getOnePa("b6c5b42a-8a07-436f-96ce-8c2ab7f4dbd2").block();
        PaInfoDto paInfo = externalRegistriesClient.getOnePa("12345").block();
        Assertions.assertNotNull(paInfo);
        Assertions.assertEquals(paInfo.getId(), "12345");
    }

    //@Test
    void testWithNullPaId() {

        //Assertions.assertEquals(exception.getStatusCode().value(), HttpStatus.BAD_REQUEST.value());
    }

    //@Test
    void testErrorPaIdNotFound(){
        externalRegistriesClient.getOnePa("12345")
                .onErrorResume(WebClientResponseException.class, ex -> {
                    Assertions.assertEquals(ex.getStatusCode(), HttpStatus.NOT_FOUND);
                    return Mono.empty();
                }).block();
    }

    void initialize(){
        pa.setId("12345");
        pa.setTaxId("taxId");
        pa.setIpaCode("ipaCode");
    }

}