package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnBilling;
import it.pagopa.pn.platform.rest.v1.dto.Billing;
import it.pagopa.pn.platform.rest.v1.dto.ProfilingDetail;

import java.util.Date;

public class BillingMapper {
    private BillingMapper() {
        throw new IllegalCallerException();
    }

    public static PnBilling dtoToBilling (String paId, String referenceYear, String status, Billing billing){
        PnBilling pnBilling = new PnBilling();
        pnBilling.setPaId(paId);
        pnBilling.setDescription(billing.getDescription());
        pnBilling.setStatus(status);
        pnBilling.setSdiCode(billing.getSdiCode());
        pnBilling.setMailAddress(billing.getMailAddress());
        pnBilling.setSplitPayment(billing.getSplitPayment());
        pnBilling.setReferenceYear(referenceYear);
        return pnBilling;
    }

    public static ProfilingDetail billingToDTO (PnBilling pnBilling, Billing billing){
        ProfilingDetail profilingDetail = new ProfilingDetail();
        profilingDetail.setBilling(billing);
//        profilingDetail.setDeadlineDate(Date.from(pnBilling.getDeadlineDate()));
        profilingDetail.setStatus(ProfilingDetail.StatusEnum.fromValue(pnBilling.getStatus()));
        profilingDetail.setReferenceYear(pnBilling.getReferenceYear());
        return profilingDetail;
    }


}
