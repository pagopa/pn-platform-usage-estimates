package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnPublicAdministration;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDto;
import it.pagopa.pn.platform.rest.v1.dto.EstimateSearchTableDTO;
import it.pagopa.pn.platform.rest.v1.dto.PageableEstimateResponseDto;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;

public class EstimateMapper {

    private EstimateMapper() {
        throw new IllegalCallerException();
    }

    public static PageableEstimateResponseDto toPageableResponse(PageModel<PnEstimate> pagePnEstimate) {
        PageableEstimateResponseDto pageableEstimateResponseDto = new PageableEstimateResponseDto();
        pageableEstimateResponseDto.setPageable(pagePnEstimate.getPageable());
        pageableEstimateResponseDto.setNumber(pagePnEstimate.getNumber());
        pageableEstimateResponseDto.setNumberOfElements(pagePnEstimate.getNumberOfElements());
        pageableEstimateResponseDto.setSize(pagePnEstimate.getSize());
        pageableEstimateResponseDto.setTotalElements(pagePnEstimate.getTotalElements());
        pageableEstimateResponseDto.setTotalPages((long) pagePnEstimate.getTotalPages());
        pageableEstimateResponseDto.setFirst(pagePnEstimate.isFirst());
        pageableEstimateResponseDto.setLast(pagePnEstimate.isLast());
        pageableEstimateResponseDto.setEmpty(pagePnEstimate.isEmpty());
        pageableEstimateResponseDto.setContent(pagePnEstimate.mapTo(EstimateMapper::estimatesToDto));
        return pageableEstimateResponseDto;
    }

    public static EstimateSearchTableDTO estimatesToDto(PnEstimate estimates){
        EstimateSearchTableDTO estimatesList = new EstimateSearchTableDTO();
        estimatesList.setReferenceMonth(estimates.getReferenceMonth());
        estimatesList.setLastModifiedTimestamp(Date.from(Instant.now()));
        estimatesList.setStatus(EstimateSearchTableDTO.StatusEnum.fromValue(estimates.getStatus()));
        //estimatesList.setCheckPDND(true);
        return estimatesList;
    }

    public static EstimateDto estimateDetailToDto(PnEstimate pnEstimate) {
        EstimateDto estimateDto = new EstimateDto();
        estimateDto.setPaId(pnEstimate.getPaId());
        estimateDto.setStatus(EstimateDto.StatusEnum.fromValue(pnEstimate.getStatus()));
        estimateDto.setReferenceMonth(pnEstimate.getReferenceMonth());
        return estimateDto;
    }

    public static PageModel<PnEstimate> toPagination(Pageable pageable, List<PnEstimate> list){
        return PageModel.builder(list, pageable);
    }
}
