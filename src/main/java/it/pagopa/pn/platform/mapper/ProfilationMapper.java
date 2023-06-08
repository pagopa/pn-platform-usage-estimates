package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.rest.v1.dto.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

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

}
