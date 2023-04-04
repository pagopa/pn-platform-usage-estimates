package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.mapper.BillingMapper;
import it.pagopa.pn.platform.middleware.db.dao.BillingDAO;
import it.pagopa.pn.platform.rest.v1.dto.Billing;
import it.pagopa.pn.platform.rest.v1.dto.Profiling;
import it.pagopa.pn.platform.rest.v1.dto.ProfilingDetail;
import it.pagopa.pn.platform.service.ProfilationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ProfilationServiceImpl implements ProfilationService {

    @Autowired
    private BillingDAO billingDAO;

    @Override
    public Mono<ProfilingDetail> createOrUpdateBilling(String paId, String referenceYear, String status, Billing data) {
        return billingDAO.createOrUpdate(BillingMapper.dtoToBilling(paId, referenceYear, status, data))
                .map(entity -> BillingMapper.billingToDTO(entity, data));
    }

    @Override
    public Mono<Profiling> getProfilationDetail(String paId) {
        return null;
    }
}
