package it.pagopa.pn.platform.mapper;


import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.model.PageModel;
import it.pagopa.pn.platform.model.TimelineProfilation;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
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

    public static ProfilationDetail profilationDetailToDto(PnProfilation pnProfilation, PaInfoDto paInfoDto){
        ProfilationDetail profilationDetail = new ProfilationDetail();
        PAInfo paInfo = new PAInfo();
        Billing billing = new Billing();

        //INFO PA
        paInfo.setPaId(paInfoDto.getId());
        paInfo.setPaName(paInfoDto.getName());
        paInfo.setTaxId(paInfoDto.getTaxId());

        //FATTURAZIONE
        billing.setMailAddress(pnProfilation.getMailAddress());
        billing.setDescription(pnProfilation.getDescription());
        billing.setSplitPayment(pnProfilation.getSplitPayment());

        //PERIODO
        profilationDetail.setPaInfo(paInfo);
        profilationDetail.setBilling(billing);

        profilationDetail.setStatus(ProfilationDetail.StatusEnum.fromValue(pnProfilation.getStatus()));
        profilationDetail.setReferenceYear(pnProfilation.getReferenceYear());
        if (pnProfilation.getLastModifiedDate() != null){
            profilationDetail.setLastModifiedDate(Date.from(pnProfilation.getLastModifiedDate()));
        }
        profilationDetail.setDeadlineDate(Date.from(pnProfilation.getDeadlineDate()));
        profilationDetail.setShowEdit(pnProfilation.getDeadlineDate().isAfter(Instant.now()));

        return profilationDetail;
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


    public static ProfilationHistory profilationsToDto(PnProfilation entity){
        ProfilationHistory dto = new ProfilationHistory();
        dto.setReferenceYear(entity.getReferenceYear());
        dto.setDeadlineDate(Date.from(entity.getDeadlineDate()));
        dto.setLastModifiedDate(entity.getLastModifiedDate() != null ? Date.from(entity.getLastModifiedDate()) : null);
        if (entity.getStatus().equalsIgnoreCase(ProfilationHistory.StatusEnum.DRAFT.toString())){
            dto.setStatus(ProfilationHistory.StatusEnum.ABSENT);
        }else {
            dto.setStatus(ProfilationHistory.StatusEnum.fromValue(entity.getStatus()));
        }
        dto.setShowEdit(entity.getDeadlineDate().isAfter(Instant.now()));
        return dto;
    }


    public static PageModel<PnProfilation> toPagination(org.springframework.data.domain.Pageable pageable, List<PnProfilation> list){
        return PageModel.builder(list, pageable);
    }

}
