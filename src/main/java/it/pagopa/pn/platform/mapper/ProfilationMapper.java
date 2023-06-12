package it.pagopa.pn.platform.mapper;


import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.model.TimelineProfilation;
import it.pagopa.pn.platform.rest.v1.dto.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class ProfilationMapper {

    private ProfilationMapper() {
        throw new IllegalCallerException();
    }

    public static PnProfilation dtoToPnProfilation(PnProfilation pnProfilation, String status, ProfilationCreateBody profilation) {

        pnProfilation.setStatus(status);

        //dati di fatturazione
        pnProfilation.setDescription(profilation.getDescription());
        pnProfilation.setMailAddress(profilation.getMailAddress());
        pnProfilation.setSplitPayment(profilation.getSplitPayment());
        pnProfilation.setLastModifiedDate(Instant.now().truncatedTo(ChronoUnit.SECONDS));

        return pnProfilation;
    }

    public static ProfilationPeriod profilationPeriodToDto(PnProfilation pnProfilation) {
        ProfilationPeriod profilationPeriod = new ProfilationPeriod();
        Billing billing = new Billing();

        //FATTURAZIONE
        billing.setMailAddress(pnProfilation.getMailAddress());
        billing.setDescription(pnProfilation.getDescription());
        billing.setSplitPayment(pnProfilation.getSplitPayment());

        //PERIODO
        profilationPeriod.setBilling(billing);

        profilationPeriod.setStatus(ProfilationPeriod.StatusEnum.fromValue(pnProfilation.getStatus()));
        profilationPeriod.setReferenceYear(pnProfilation.getReferenceYear());
        profilationPeriod.setLastModifiedDate(Date.from(pnProfilation.getLastModifiedDate()));
        profilationPeriod.setDeadlineDate(Date.from(pnProfilation.getDeadlineDate()));
        profilationPeriod.setShowEdit(pnProfilation.getDeadlineDate().isAfter(Instant.now()));

        return profilationPeriod;
    }

    public static PageableProfilationResponseDto toPageableResponse(org.springframework.data.domain.Pageable pageable, TimelineProfilation timelineProfilation) {
        PageableProfilationResponseDto pageableProfilationResponseDto = new PageableProfilationResponseDto();
        ProfilationPeriod actual = new ProfilationPeriod();
        PageForProfilation page = new PageForProfilation();
        pageableProfilationResponseDto.setActual(actual);
        pageableProfilationResponseDto.setHistory(page);
        pageableProfilationResponseDto.getActual().setStatus(ProfilationPeriod.StatusEnum.fromValue(timelineProfilation.getActual().getStatus()));
        pageableProfilationResponseDto.getActual().setReferenceYear(timelineProfilation.getActual().getReferenceYear());
            pageableProfilationResponseDto.getActual().setDeadlineDate(Date.from(timelineProfilation.getActual().getDeadlineDate()));
        if (timelineProfilation.getActual().getLastModifiedDate() != null){
            pageableProfilationResponseDto.getActual().setLastModifiedDate(Date.from(timelineProfilation.getActual().getLastModifiedDate()));
        }
        pageableProfilationResponseDto.getActual().setShowEdit(timelineProfilation.getActual().getDeadlineDate().isAfter(Instant.now()));
        PageModel<PnProfilation> pagePnProfilation = toPagination(pageable, timelineProfilation.getHistory());
        pageableProfilationResponseDto.getHistory().setPageable(pagePnProfilation.getPageable());
        pageableProfilationResponseDto.getHistory().setNumber(pagePnProfilation.getNumber());
        pageableProfilationResponseDto.getHistory().setNumberOfElements(pagePnProfilation.getNumberOfElements());
        pageableProfilationResponseDto.getHistory().setSize(pagePnProfilation.getSize());
        pageableProfilationResponseDto.getHistory().setTotalElements(pagePnProfilation.getTotalElements());
        pageableProfilationResponseDto.getHistory().setTotalPages((long) pagePnProfilation.getTotalPages());
        pageableProfilationResponseDto.getHistory().setFirst(pagePnProfilation.isFirst());
        pageableProfilationResponseDto.getHistory().setLast(pagePnProfilation.isLast());
        pageableProfilationResponseDto.getHistory().setEmpty(pagePnProfilation.isEmpty());
        pageableProfilationResponseDto.getHistory().setContent(pagePnProfilation.mapTo(ProfilationMapper::profilationsToDto));
        return pageableProfilationResponseDto;
    }


    public static ProfilationHistory profilationsToDto(PnProfilation profilations){
        ProfilationHistory profilationsList = new ProfilationHistory();
        profilationsList.setReferenceYear(profilations.getReferenceYear());
        profilationsList.setDeadlineDate(Date.from(profilations.getDeadlineDate()));
        profilationsList.setLastModifiedDate(profilations.getLastModifiedDate() != null ? Date.from(profilations.getLastModifiedDate()) : null);
        if (profilations.getStatus().equalsIgnoreCase(ProfilationHistory.StatusEnum.DRAFT.toString())){
            profilationsList.setStatus(ProfilationHistory.StatusEnum.ABSENT);
        }else {
            profilationsList.setStatus(ProfilationHistory.StatusEnum.fromValue(profilations.getStatus()));
        }
        return profilationsList;
    }


    public static PageModel<PnProfilation> toPagination(org.springframework.data.domain.Pageable pageable, List<PnProfilation> list){
        return PageModel.builder(list, pageable);
    }

}
