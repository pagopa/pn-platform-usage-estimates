package it.pagopa.pn.platform.rest;

import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.service.ReportService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@WebFluxTest(controllers = {ReportApiController.class})
public class ReportApiControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private ReportService reportService;

    @Test
    public void downloadReportFile() {

        InfoDownloadDTO response = new InfoDownloadDTO();
        String path = "/pn-usage-estimates/estimate/12345/reports/A12C34D56789E0";

        Mockito.when(this.reportService.downloadReportFile(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).queryParam("type", "SOURCE").build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getAllReportFile() {
        Flux<InfoDownloadDTO> response = Flux.empty();
        String path = "/pn-usage-estimates/12345/reports/APR-2023";

        Mockito.when(this.reportService.getAllReportFile(Mockito.any(), Mockito.any()))
                .thenReturn(response);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getAllDeanonymizedFiles(){
        PageableDeanonymizedFilesResponseDto response = new PageableDeanonymizedFilesResponseDto();
        String path = "/pn-usage-estimates/reports/12345";

        Mockito.when(this.reportService.getAllDeanonymizedFiles(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just(response));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).build())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getScheduleDeanonymizedFiles(){

        String path = "/pn-usage-estimates/reports/12345/schedule/A12C34D56789E0";

        Mockito.when(this.reportService.getScheduleDeanonymizedFiles(Mockito.any(), Mockito.any()))
                .thenReturn(Mono.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path).queryParam("type", "SOURCE").build())
                .exchange()
                .expectStatus().isOk();
    }

}
