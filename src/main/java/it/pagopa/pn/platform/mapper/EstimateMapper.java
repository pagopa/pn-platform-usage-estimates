package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.datalake.v1.dto.MonthlyNotificationPreorderDto;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.model.TimelineEstimate;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.rest.v1.dto.*;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class EstimateMapper {

    static Integer DEFAULT_VALUE = 0;

    private EstimateMapper() {
        throw new IllegalCallerException();
    }

    public static PageableEstimateResponseDto toPageableResponse(Pageable pageable, TimelineEstimate timelineEstimate) {
        PageableEstimateResponseDto pageableEstimateResponseDto = new PageableEstimateResponseDto();
        EstimatePeriod actual = new EstimatePeriod();
        Page page = new Page();
        pageableEstimateResponseDto.setActual(actual);
        pageableEstimateResponseDto.setHistory(page);
        pageableEstimateResponseDto.getActual().setStatus(EstimatePeriod.StatusEnum.fromValue(timelineEstimate.getActual().getStatus()));
        pageableEstimateResponseDto.getActual().setReferenceMonth(timelineEstimate.getActual().getReferenceMonth());
        pageableEstimateResponseDto.getActual().setDeadlineDate(Date.from(timelineEstimate.getActual().getDeadlineDate()));
        if (timelineEstimate.getActual().getLastModifiedDate() != null){
            pageableEstimateResponseDto.getActual().setLastModifiedDate(Date.from(timelineEstimate.getActual().getLastModifiedDate()));
        }
        pageableEstimateResponseDto.getActual().setShowEdit(timelineEstimate.getActual().getDeadlineDate().isAfter(Instant.now()));
        setActualInteger(timelineEstimate, pageableEstimateResponseDto);
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

        pageableEstimateResponseDto.getHistory().setContent(pagePnEstimate.mapTo(EstimateMapper::estimatesToDto));
        return pageableEstimateResponseDto;
    }

    private static void setActualInteger(TimelineEstimate timelineEstimate, PageableEstimateResponseDto pageableEstimateResponseDto) {
        Estimate estimate = new Estimate();
        pageableEstimateResponseDto.getActual().setEstimate(estimate);
        if (timelineEstimate.getActual().getTotal890Notif() == null){
            pageableEstimateResponseDto.getActual().getEstimate().setTotal890Notif(DEFAULT_VALUE);
        }
        else {
            pageableEstimateResponseDto.getActual().getEstimate().setTotal890Notif(timelineEstimate.getActual().getTotal890Notif());
        }
        if (timelineEstimate.getActual().getTotalAnalogNotif() == null){
            pageableEstimateResponseDto.getActual().getEstimate().setTotalAnalogNotif(DEFAULT_VALUE);
        }
        else  {
            pageableEstimateResponseDto.getActual().getEstimate().setTotalAnalogNotif(timelineEstimate.getActual().getTotalAnalogNotif());
        }

        if (timelineEstimate.getActual().getTotalDigitalNotif() == null){
            pageableEstimateResponseDto.getActual().getEstimate().setTotalDigitalNotif(DEFAULT_VALUE);
        }
        else {
            pageableEstimateResponseDto.getActual().getEstimate().setTotalDigitalNotif(timelineEstimate.getActual().getTotalDigitalNotif());

        }
    }

    public static EstimateHistory estimatesToDto(PnEstimate entity){
        EstimateHistory dto = new EstimateHistory();
        dto.setPaId(entity.getPaId());
        dto.setReferenceMonth(entity.getReferenceMonth());
        dto.setDeadlineDate(Date.from(entity.getDeadlineDate()));
        dto.setLastModifiedDate(entity.getLastModifiedDate() != null ? Date.from(entity.getLastModifiedDate()) : null);
        if (entity.getStatus().equalsIgnoreCase(EstimateHistory.StatusEnum.DRAFT.toString())){
            dto.setStatus(EstimateHistory.StatusEnum.ABSENT);
        } else {
            dto.setStatus(EstimateHistory.StatusEnum.fromValue(entity.getStatus()));
        }
        dto.setShowEdit(entity.getDeadlineDate().isAfter(Instant.now()));
        return dto;
    }

    public static EstimatePeriod estimatePeriodToDto(PnEstimate pnEstimate) {
        EstimatePeriod estimatePeriod = new EstimatePeriod();
        Estimate estimate = new Estimate();
        Profilation billing = new Profilation();

        //STIME
        estimate.setTotalDigitalNotif(pnEstimate.getTotalDigitalNotif());
        estimate.setTotal890Notif(pnEstimate.getTotal890Notif());
        estimate.setTotalAnalogNotif(pnEstimate.getTotalAnalogNotif());


        //FATTURAZIONE
        billing.setMailAddress(pnEstimate.getMailAddress());
        billing.setDescription(pnEstimate.getDescription());
        billing.setSplitPayment(pnEstimate.getSplitPayment());

        //PERIODO
        estimatePeriod.setEstimate(estimate);
        estimatePeriod.setBilling(billing);

        estimatePeriod.setStatus(EstimatePeriod.StatusEnum.fromValue(pnEstimate.getStatus()));
        estimatePeriod.setReferenceMonth(pnEstimate.getReferenceMonth());
        estimatePeriod.setLastModifiedDate(Date.from(pnEstimate.getLastModifiedDate()));
        estimatePeriod.setDeadlineDate(Date.from(pnEstimate.getDeadlineDate()));
        estimatePeriod.setShowEdit(pnEstimate.getDeadlineDate().isAfter(Instant.now()));

        return estimatePeriod;
    }


    public static EstimateDetail estimateDetailToDto(PnEstimate pnEstimate, PaInfoDto paInfoDto) {
        EstimateDetail estimateDetail = new EstimateDetail();
        Estimate estimate = new Estimate();
        PAInfo paInfo = new PAInfo();
        Profilation profilation = new Profilation();

        //INFO PA
        paInfo.setPaId(paInfoDto.getId());
        paInfo.setPaName(paInfoDto.getName());
        paInfo.setTaxId(paInfoDto.getTaxId());

        //STIME
        estimate.setTotalDigitalNotif(pnEstimate.getTotalDigitalNotif());
        estimate.setTotal890Notif(pnEstimate.getTotal890Notif());
        estimate.setTotalAnalogNotif(pnEstimate.getTotalAnalogNotif());


        //FATTURAZIONE
        profilation.setMailAddress(pnEstimate.getMailAddress());
        profilation.setDescription(pnEstimate.getDescription());
        profilation.setSplitPayment(pnEstimate.getSplitPayment());

        //PERIODO
        estimateDetail.setEstimate(estimate);
        estimateDetail.setPaInfo(paInfo);
        estimateDetail.setBilling(profilation);

        estimateDetail.setStatus(EstimateDetail.StatusEnum.fromValue(pnEstimate.getStatus()));
        estimateDetail.setReferenceMonth(pnEstimate.getReferenceMonth());
        if (pnEstimate.getLastModifiedDate() != null){
            estimateDetail.setLastModifiedDate(Date.from(pnEstimate.getLastModifiedDate()));
        }
        estimateDetail.setDeadlineDate(Date.from(pnEstimate.getDeadlineDate()));
        estimateDetail.setShowEdit(pnEstimate.getDeadlineDate().isAfter(Instant.now()));

        return estimateDetail;
    }

    public static PnEstimate dtoToPnEstimate(PnEstimate pnEstimate, String status, EstimateCreateBody estimate) {

        pnEstimate.setStatus(status);

        //dati stima
        pnEstimate.setTotalDigitalNotif(estimate.getTotalDigitalNotif());
        pnEstimate.setTotal890Notif(estimate.getTotal890Notif());
        pnEstimate.setTotalAnalogNotif(estimate.getTotalAnalogNotif());

        //dati di fatturazione
        pnEstimate.setDescription(estimate.getDescription());
        pnEstimate.setMailAddress(estimate.getMailAddress());
        pnEstimate.setSplitPayment(estimate.getSplitPayment());
        pnEstimate.setLastModifiedDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        return pnEstimate;
    }

    public static MonthlyNotificationPreorderDto dtoToFile (PnEstimate pnEstimate){
        MonthlyNotificationPreorderDto monthlyNotificationPreorderDto = new MonthlyNotificationPreorderDto();
        monthlyNotificationPreorderDto.setAnalogNotifications890(pnEstimate.getTotal890Notif());
        monthlyNotificationPreorderDto.setAnalogNotificationsAR(pnEstimate.getTotalAnalogNotif());
        monthlyNotificationPreorderDto.setDigitalNotifications(pnEstimate.getTotalDigitalNotif());
        monthlyNotificationPreorderDto.setSplitPayment(pnEstimate.getSplitPayment().toString());
        monthlyNotificationPreorderDto.setAdministrativeEmail(pnEstimate.getMailAddress());
        monthlyNotificationPreorderDto.setReferenceMonth(pnEstimate.getReferenceMonth());
        monthlyNotificationPreorderDto.selfCarePaId(pnEstimate.getPaId());
        monthlyNotificationPreorderDto.setRecordCreationDate(pnEstimate.getLastModifiedDate().toString());
        monthlyNotificationPreorderDto.setRecordFormatVersion(BigDecimal.ONE);
        int count = 1;
        if (pnEstimate.getRecordVersion() == null){
            pnEstimate.setRecordVersion(count);
        }else {
            count = pnEstimate.getRecordVersion()+1;
            pnEstimate.setRecordVersion(count);
        }
        monthlyNotificationPreorderDto.setRecordVersion(new BigDecimal(pnEstimate.getRecordVersion()));
        return monthlyNotificationPreorderDto;
    }

    public static PageModel<PnEstimate> toPagination(Pageable pageable, List<PnEstimate> list){
        return PageModel.builder(list, pageable);
    }

}
