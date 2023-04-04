package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.rest.v1.dto.*;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
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

    public static EstimateDetail estimateDetailToDto(PnEstimate pnEstimate, PaInfoDto paInfoDto) {
        EstimateDetail estimateDetail = new EstimateDetail();
        Estimate estimate = new Estimate();
        PAInfo paInfo = new PAInfo();

        //INFO PA
        paInfo.setPaId(paInfoDto.getId());
        paInfo.setPaName(paInfoDto.getName());
        paInfo.setTaxId(paInfoDto.getTaxId());

        //STIME
        estimate.setTotalDigitalNotif(pnEstimate.getTotalDigitalNotif());
        estimate.setTotalPaper890Notif(pnEstimate.getTotalPaper890Notif());
        estimate.setTotalPaperInternationalNotif(pnEstimate.getTotalPaperInternationalNotif());
        estimate.setTotalPaperNationalNotif(pnEstimate.getTotalPaperNationalNotif());

        //PERIODO
        estimateDetail.setEstimate(estimate);
        estimateDetail.setStatus(EstimateDetail.StatusEnum.fromValue(pnEstimate.getStatus()));
        estimateDetail.setReferenceMonth(pnEstimate.getReferenceMonth());
        estimateDetail.setLastModifiedTimestamp(Date.from(Instant.now()));
        estimateDetail.setDeadlineDate(Date.from(Instant.now()));
        estimateDetail.showEdit(true);
        estimateDetail.setPaInfo(paInfo);

        return estimateDetail;
    }

    public static PnEstimate dtoToPnEstimate(String status, String paId, String referenceMonth, Estimate estimate) {
        PnEstimate pnEstimate = new PnEstimate();

        pnEstimate.setStatus(status);
        pnEstimate.setPaId(paId);
        pnEstimate.setReferenceMonth(referenceMonth);
        pnEstimate.setTotalDigitalNotif(estimate.getTotalDigitalNotif());
        pnEstimate.setTotalPaper890Notif(estimate.getTotalPaper890Notif());
        pnEstimate.setTotalPaperInternationalNotif(estimate.getTotalPaperInternationalNotif());
        pnEstimate.setTotalPaperNationalNotif(estimate.getTotalPaperNationalNotif());

        return pnEstimate;
    }

    public static PageModel<PnEstimate> toPagination(Pageable pageable, List<PnEstimate> list){
        return PageModel.builder(list, pageable);
    }
}
