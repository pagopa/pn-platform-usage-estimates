package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.dto.*;
import it.pagopa.pn.platform.service.ProfilationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = {ProfilationApiController.class})
class ProfilationApiControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ProfilationService profilationService;

    @Test
    void createOrUpdateProfilation() {
        ProfilationPeriod response = new ProfilationPeriod();
        String path = "/pn-usage-estimates/12345/profilation/2023";
        Mockito.when(profilationService.createOrUpdateProfilation(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam("status", "VALIDATED")
                        .build())
                .bodyValue(getProfilationCreateBody())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void validated(){
        ProfilationPeriod response = new ProfilationPeriod();
        String path = "/pn-usage-estimates/12345/profilation/2023/validated";
        Mockito.when(profilationService.validatedProfilation(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    void getAllProfilations() {
        PageableProfilationResponseDto response = new PageableProfilationResponseDto();
        String path = "/pn-usage-estimates/profilations";
        Mockito.when(profilationService.getAllProfilations(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getProfilationDetail() {
        ProfilationDetail response = new ProfilationDetail();
        String path = "/pn-usage-estimates/profilation/12345/detail/2023";
        Mockito.when(profilationService.getProfilationDetail(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }

    private ProfilationCreateBody getProfilationCreateBody(){
        ProfilationCreateBody body = new ProfilationCreateBody();
        body.setDescription("description");
        body.setMailAddress("mailAddress");
        body.setSplitPayment(true);
        return body;
    }
}
