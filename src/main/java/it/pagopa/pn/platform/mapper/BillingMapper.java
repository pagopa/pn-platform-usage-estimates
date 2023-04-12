package it.pagopa.pn.platform.mapper;

import it.pagopa.pn.platform.middleware.db.entities.PnBilling;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.rest.v1.dto.Billing;
import it.pagopa.pn.platform.rest.v1.dto.PAInfo;
import it.pagopa.pn.platform.rest.v1.dto.Profiling;
import it.pagopa.pn.platform.rest.v1.dto.ProfilingDetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BillingMapper {
    private BillingMapper() {
        throw new IllegalCallerException();
    }

    public static PnEstimate dtoToBilling (String paId, String referenceMonth, String status, Billing billing){
        PnEstimate pnBilling = new PnEstimate();
        pnBilling.setPaId(paId);
        pnBilling.setStatus(status);
        pnBilling.setReferenceMonth(referenceMonth);

        pnBilling.setSdiCode(billing.getSdiCode());
        pnBilling.setMailAddress(billing.getMailAddress());
        pnBilling.setSplitPayment(billing.getSplitPayment());
        pnBilling.setDescription(billing.getDescription());


        return pnBilling;
    }

    public static ProfilingDetail billingToDTO (PnBilling pnBilling){
        ProfilingDetail profilingDetail = new ProfilingDetail();
        Billing billing = new Billing();

        billing.setDescription(pnBilling.getDescription());
        billing.setMailAddress(pnBilling.getMailAddress());
        billing.setSdiCode(pnBilling.getSdiCode());
        billing.setSplitPayment(pnBilling.getSplitPayment());
        profilingDetail.setBilling(billing);

        //profilingDetail.setDeadlineDate(Date.from(pnBilling.getDeadlineDate()));
        profilingDetail.setStatus(ProfilingDetail.StatusEnum.fromValue(pnBilling.getStatus()));
        profilingDetail.setReferenceYear(pnBilling.getReferenceYear());


        return profilingDetail;
    }

    public static Profiling billingsToDto (PnBilling pnBilling, PaInfoDto paInfoDto){

        Profiling profiling = new Profiling();
        ProfilingDetail profilingDetail = new ProfilingDetail();
        List<ProfilingDetail> list = new ArrayList<>();
        PAInfo paInfo = new PAInfo();
        Billing billing = new Billing();

        //FATTURAZIONE
        billing.setSplitPayment(pnBilling.getSplitPayment());
        billing.setDescription(pnBilling.getDescription());
        billing.setSdiCode(pnBilling.getSdiCode());
        billing.setMailAddress(pnBilling.getMailAddress());

        //PERIODO
        profilingDetail.setBilling(billing);
        profilingDetail.setStatus(ProfilingDetail.StatusEnum.fromValue(pnBilling.getStatus()));
        profilingDetail.setDeadlineDate(Date.from(pnBilling.getDeadlineDate()));
        profilingDetail.setReferenceYear(pnBilling.getReferenceYear());
        profilingDetail.setShowEdit(true);

        list.add(profilingDetail);

        //PA INFO
        paInfo.setPaId(paInfoDto.getId());
        paInfo.setPaName(paInfoDto.getName());
        paInfo.setTaxId(paInfoDto.getTaxId());
        profiling.setProfiles(list);
        profiling.setPaInfo(paInfo);

        return  profiling;

    }


}

