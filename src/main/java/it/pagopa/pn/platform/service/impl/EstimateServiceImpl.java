package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.EstimateMapper;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.dao.PublicAdministrationDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnPublicAdministration;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDto;
import it.pagopa.pn.platform.rest.v1.dto.InfoDownloadDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import it.pagopa.pn.platform.service.EstimateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.ESTIMATE_NOT_EXISTED;


@Slf4j
@Service
public class EstimateServiceImpl implements EstimateService {

    @Autowired
    private EstimateDAO estimateDAO;

    @Autowired
    private PublicAdministrationDAO publicAdministrationDAO;

    @Override
    public Mono<EstimateDto> createOrUpdateEstimate(EstimateDto dto) {
        return null;
    }

    @Override
    public Mono<InfoDownloadDTO> downloadEstimateFile(String paId, String fileId) {
        return null;
    }

    @Override
    public Mono<PageableEstimateResponseDto> getAllEstimate(String paId, String taxId, String ipaId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);

        return estimateDAO.getAllEstimates(paId)
                .map(list -> EstimateMapper.toPagination(pageable, list))
                .map(EstimateMapper::toPageableResponse);
    }

    @Override
    public Mono<EstimateDto> getEstimateDetail(String paId, String referenceMonth) {
        return this.estimateDAO.getEstimate(paId)
                .switchIfEmpty(Mono.error(new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage())))
                .zipWhen(pnEstimate -> publicAdministrationDAO.getPaDetail(paId, referenceMonth)
                        .map(publicAdmin -> publicAdmin)
                        .switchIfEmpty(Mono.just(new PnPublicAdministration())))
                .map(detailEstimateAndPublicAdmin ->
                        EstimateMapper.estimateDetailToDto(detailEstimateAndPublicAdmin.getT1(), detailEstimateAndPublicAdmin.getT2()));
    }

    @Override
    public Mono<Flux<InfoDownloadDTO>> getAllEstimateFile(String paId) {
        return null;
    }
}
