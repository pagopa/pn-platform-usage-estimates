package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.model.TimelineEstimate;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.rest.v1.dto.*;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.List;

public class EstimateMapper {

    private EstimateMapper() {
        throw new IllegalCallerException();
    }

    public static PageableEstimateResponseDto toPageableResponse(Pageable pageable, TimelineEstimate timelineEstimate) {
        PageableEstimateResponseDto pageableEstimateResponseDto = new PageableEstimateResponseDto();
        pageableEstimateResponseDto.getActual().setStatus(EstimateSearchTableDTO.StatusEnum.fromValue(timelineEstimate.getActual().getStatus()));
        pageableEstimateResponseDto.getActual().setReferenceMonth(timelineEstimate.getActual().getReferenceMonth());
        pageableEstimateResponseDto.getActual().setDeadlineDate(timelineEstimate.getActual().getDeadlineDate().toString());
        pageableEstimateResponseDto.getActual().setLastModifiedDate(Date.from(timelineEstimate.getActual().getLastModifiedTimestamp()));
        PageModel<PnEstimate> pagePnEstimate = toPagination(pageable, timelineEstimate.getHistory());
        pageableEstimateResponseDto.getHistory().setPageable(pagePnEstimate.getPageable());
        pageableEstimateResponseDto.getHistory().setNumber(pagePnEstimate.getNumber());
        pageableEstimateResponseDto.getHistory().setNumberOfElements(pagePnEstimate.getNumberOfElements());
        pageableEstimateResponseDto.getHistory().setSize(pagePnEstimate.getSize());
        pageableEstimateResponseDto.getHistory().setTotalElements(pagePnEstimate.getTotalElements());
        pageableEstimateResponseDto.getHistory().setTotalPages((long) pagePnEstimate.getTotalPages());
        pageableEstimateResponseDto.getHistory().setFirst(pagePnEstimate.isFirst());
        pageableEstimateResponseDto.getHistory().setLast(pagePnEstimate.isLast());
        pageableEstimateResponseDto.getHistory().setEmpty(pagePnEstimate.isEmpty());
        pageableEstimateResponseDto.setContent(pagePnEstimate.mapTo(EstimateMapper::estimatesToDto));
        return pageableEstimateResponseDto;
    }

    public static EstimateSearchTableDTO estimatesToDto(PnEstimate estimates){
        EstimateSearchTableDTO estimatesList = new EstimateSearchTableDTO();
        estimatesList.setReferenceMonth(estimates.getReferenceMonth());
        estimatesList.setLastModifiedDate(Date.from(Instant.now()));
        estimatesList.setStatus(EstimateSearchTableDTO.StatusEnum.fromValue(estimates.getStatus()));
        //estimatesList.setCheckPDND(true);
        return estimatesList;
    }

    public static EstimateDetail estimateDetailToDto(PnEstimate pnEstimate, PaInfoDto paInfoDto) {
        EstimateDetail estimateDetail = new EstimateDetail();
        Estimate estimate = new Estimate();
        PAInfo paInfo = new PAInfo();
        Billing billing = new Billing();

        //INFO PA
        paInfo.setPaId(paInfoDto.getId());
        paInfo.setPaName(paInfoDto.getName());
        paInfo.setTaxId(paInfoDto.getTaxId());

        //STIME
        estimate.setTotalDigitalNotif(pnEstimate.getTotalDigitalNotif());
        estimate.setTotal890Notif(pnEstimate.getTotal890Notif());
        estimate.setTotalAnalogNotif(pnEstimate.getTotalAnalogNotif());


        //FATTURAZIONE
        billing.setMailAddress(pnEstimate.getMailAddress());
        billing.setDescription(pnEstimate.getDescription());
        billing.setSplitPayment(pnEstimate.getSplitPayment());

        //PERIODO
        estimateDetail.setEstimate(estimate);
        estimateDetail.setPaInfo(paInfo);
        estimateDetail.setBilling(billing);

        estimateDetail.setStatus(EstimateDetail.StatusEnum.fromValue(pnEstimate.getStatus()));
        estimateDetail.setReferenceMonth(pnEstimate.getReferenceMonth());
        estimateDetail.setLastModifiedDate(Date.from(Instant.now()));
        estimateDetail.setDeadlineDate(Date.from(pnEstimate.getDeadlineDate()));

//        if ((pnEstimate.getDeadlineDate().isAfter(Instant.now())))
//            estimateDetail.showEdit(false);
//        if ((pnEstimate.getDeadlineDate().isBefore(Instant.now())))
//            estimateDetail.showEdit(true);


        return estimateDetail;
    }


    public static PnEstimate dtoToPnEstimateDefault(String paId, String referenceMonth){
        PnEstimate pnEstimate = new PnEstimate();
        pnEstimate.setPaId(paId);
        pnEstimate.setReferenceMonth(referenceMonth);
        pnEstimate.setLastModifiedTimestamp(Instant.now());
        //data di scadenza sulla base di data odierna
        pnEstimate.setDeadlineDate(Instant.now().plus(Period.ofDays(30)));
        pnEstimate.setStatus("IN_PROGRESS");

        return pnEstimate;
    }


    public static PnEstimate dtoToPnEstimate(String status, String paId, String referenceMonth, EstimateCreateBody estimate) {
        PnEstimate pnEstimate = new PnEstimate();

        pnEstimate.setStatus(status);
        pnEstimate.setPaId(paId);
        pnEstimate.setReferenceMonth(referenceMonth);

        //dati stima
        pnEstimate.setTotalDigitalNotif(estimate.getTotalDigitalNotif());
        pnEstimate.setTotal890Notif(estimate.getTotal890Notif());
        pnEstimate.setTotalAnalogNotif(estimate.getTotalAnalogNotif());

        //dati di fatturazione
        pnEstimate.setDescription(estimate.getDescription());
        pnEstimate.setMailAddress(estimate.getMailAddress());
        pnEstimate.setSplitPayment(estimate.getSplitPayment());

        return pnEstimate;
    }

    public static PageModel<PnEstimate> toPagination(Pageable pageable, List<PnEstimate> list){
        return PageModel.builder(list, pageable);
    }

}
