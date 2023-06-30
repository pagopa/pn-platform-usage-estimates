package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EstimateDAO {
    Mono<PnEstimate> createOrUpdate(PnEstimate data);
    Mono<List<PnEstimate>> getAllEstimates(String paId);
    Mono<PnEstimate> getEstimate(String paId);
    Mono<PnEstimate> getEstimateDetail(String paId, String referenceMonth);
    Mono<List<PnEstimate>> getAllEstimates();
}
