package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.dto.*;
import it.pagopa.pn.platform.service.EstimateService;
import it.pagopa.pn.platform.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = {EstimateApiController.class})
class EstimateApiControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private EstimateService estimateService;


    @Test
    void createOrUpdateEstimate() {
        EstimatePeriod response = new EstimatePeriod();
        String path = "/pn-usage-estimates/12345/estimate/MAR-2023";
        Mockito.when(estimateService.createOrUpdateEstimate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam("status", "VALIDATED")
                        .build())
                .bodyValue(getEstimateCreateBody())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void validated(){
        EstimatePeriod response = new EstimatePeriod();
        String path = "/pn-usage-estimates/12345/estimate/MAR-2023/validated";
        Mockito.when(estimateService.validated(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }


    //@Test
    void getAllEstimate() {
        PageableEstimateResponseDto response = new PageableEstimateResponseDto();
        String path = "/pn-usage-estimates/estimates";
        Mockito.when(estimateService.getAllEstimate(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getEstimateDetail() {
        EstimateDetail response = new EstimateDetail();
        String path = "/pn-usage-estimates/estimate/12345/detail/MAR-2023";
        Mockito.when(estimateService.getEstimateDetail(Mockito.anyString(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }


    private EstimateCreateBody getEstimateCreateBody(){
        EstimateCreateBody body = new EstimateCreateBody();
        body.setDescription("description");
        body.setMailAddress("mailAddress");
        body.setSplitPayment(true);
        body.setTotal890Notif(123);
        body.setTotalAnalogNotif(87);
        body.setTotalDigitalNotif(363);
        return body;
    }
}