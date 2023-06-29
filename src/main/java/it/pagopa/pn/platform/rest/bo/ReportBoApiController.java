package it.pagopa.pn.platform.rest.bo;

import it.pagopa.pn.platform.rest.v1.api.ReportBoApi;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.ReportDTO;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import it.pagopa.pn.platform.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ReportBoApiController implements ReportBoApi {

    @Autowired
    private ReportService reportService;

    @Override
    public Mono<ResponseEntity<ReportDTO>> downloadReportFile(String paId, String reportKey, String type, ServerWebExchange exchange) {
        return this.reportService.downloadReportFile(paId, reportKey, type).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<ReportDTO>>> getAllReportFile(String paId, String referenceMonth, ServerWebExchange exchange) {
        return this.reportService.getAllReportFile(paId, referenceMonth).collectList().map(list -> ResponseEntity.ok(Flux.fromStream(list.stream())));
    }

    @Override
    public Mono<ResponseEntity<PageableDeanonymizedFilesResponseDto>> getAllDeanonymizedFiles(String paId, ReportStatusEnum status, Integer page, Integer size, final ServerWebExchange exchange){
        return this.reportService.getAllDeanonymizedFiles(paId, status, page, size).map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> getScheduleDeanonymizedFiles(String paId, String reportKey,  final ServerWebExchange exchange){
        return this.reportService.getScheduleDeanonymizedFiles(paId, reportKey).map(ResponseEntity::ok);
    }
}
