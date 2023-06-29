package it.pagopa.pn.platform.service;


import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import it.pagopa.pn.platform.rest.v1.dto.ReportDTO;
import it.pagopa.pn.platform.rest.v1.dto.ReportStatusEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportService {

    Mono<ReportDTO> downloadReportFile(String paId, String reportKey, String type);
    Mono<PageableDeanonymizedFilesResponseDto> getAllDeanonymizedFiles(String paId, ReportStatusEnum status, Integer page, Integer size);
    Flux<ReportDTO> getAllReportFile(String paId, String referenceMonth);
    Mono<Void> getScheduleDeanonymizedFiles(String paId, String reportKey);


}
