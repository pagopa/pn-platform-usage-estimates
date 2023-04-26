package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.service.ProfilationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProfilationServiceImpl implements ProfilationService {

    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;


    /*@Override
    public Mono<ProfilingDetail> createOrUpdateBilling(String paId, String referenceYear, String status, Billing data) {
        return this.billingDAO.createOrUpdate(BillingMapper.dtoToBilling(paId, referenceYear, status, data))
                .switchIfEmpty(Mono.error(new PnGenericException(BILLING_NOT_EXIST, BILLING_NOT_EXIST.getMessage())))
                .map(BillingMapper::billingToDTO);

    }

    @Override
    public Mono<Profiling> getProfilationDetail(String paId) {
        return this.billingDAO.getProfilationDetail(paId)
                .switchIfEmpty(Mono.error(new PnGenericException(PA_ID_NOT_EXIST, PA_ID_NOT_EXIST.getMessage())))
                .zipWhen(pnBilling -> externalRegistriesClient.getOnePa(paId))
                    .map(publicAdmin -> publicAdmin)
                    .switchIfEmpty(Mono.error(new PnGenericException(PA_ID_NOT_EXIST, PA_ID_NOT_EXIST.getMessage())))
                .map(BillingAndPublicAdmin -> BillingMapper.billingsToDto(BillingAndPublicAdmin.getT1(), BillingAndPublicAdmin.getT2()));

    }*/
}
