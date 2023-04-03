package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.mapper.BillingMapper;
import it.pagopa.pn.platform.middleware.db.dao.BillingDAO;
import it.pagopa.pn.platform.rest.v1.dto.BillingDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfilationDTO;
import it.pagopa.pn.platform.rest.v1.dto.ProfiliationAndBillingDTO;
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
    public Mono<BillingDTO> createOrUpdateBilling(String paId, String referenceYear, BillingDTO data) {
        return billingDAO.createOrUpdate(BillingMapper.dtoToBilling(paId, referenceYear, data)).map(BillingMapper::billingToDTO);
    }

    @Override
    public Mono<ProfilationDTO> getProfilationDetail(String paId) {
        return null;
    }

    @Override
    public Mono<ProfiliationAndBillingDTO> getProfilationAndBillingDetail(String paId, String referenceYear) {
        return null;
    }
}
