package it.pagopa.pn.platform.service;

import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableDeanonymizedFilesResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReportService {

    Mono<InfoDownloadDTO> downloadReportFile(String paId, String reportKey, String type);
    Mono<PageableDeanonymizedFilesResponseDto> getAllDeanonymizedFiles(String paId, String status, Integer page, Integer size);
    Flux<InfoDownloadDTO> getAllReportFile(String paId, String referenceMonth);
    Mono<Void> getScheduleDeanonymizedFiles(String paId, String reportKey);


}
