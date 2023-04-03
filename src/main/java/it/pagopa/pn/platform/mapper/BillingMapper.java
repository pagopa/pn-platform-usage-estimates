package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnBilling;
import it.pagopa.pn.platform.rest.v1.dto.BillingDTO;

import java.util.Date;

public class BillingMapper {
    private BillingMapper() {
        throw new IllegalCallerException();
    }

    public static PnBilling dtoToBilling (String paId, String referenceYear, BillingDTO billingDTO){
        PnBilling billing = new PnBilling();
        billing.setPaId(billingDTO.getPaId());
        billing.setDescription(billingDTO.getDescription());
        billing.setStatus(billingDTO.getStatus().getValue());
        billing.setSdiCode(billingDTO.getSdiCode());
        billing.setMailAddress(billingDTO.getMailAddress());
        billing.setDeadlineDate(billingDTO.getDeadlineDate().toInstant());
        billing.setSplitPayment(billingDTO.getSplitPayment());
        billing.setReferenceYear(billingDTO.getReferenceYear());
        return billing;
    }

    public static BillingDTO billingToDTO (PnBilling billing){
        BillingDTO billingDTO = new BillingDTO();
        billingDTO.setPaId(billing.getPaId());
        billingDTO.setDescription(billing.getDescription());
        billingDTO.setDeadlineDate(Date.from(billing.getDeadlineDate()));
        billingDTO.setMailAddress(billing.getMailAddress());
        billingDTO.setStatus(BillingDTO.StatusEnum.fromValue(billing.getStatus()));
        billingDTO.setSdiCode(billing.getSdiCode());
        billingDTO.setSplitPayment(billing.getSplitPayment());
        billingDTO.setReferenceYear(billing.getReferenceYear());
        return billingDTO;
    }


}
