package it.pagopa.pn.platform.middleware.db.dao;

import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProfilationDAO {

    Mono<PnProfilation> createOrUpdate(PnProfilation data);
    Mono<List<PnProfilation>> getAllProfilations(String paId);
    Mono<PnProfilation> getProfilation(String paId);
    Mono<PnProfilation> getProfilationDetail(String paId, String referenceYear);
}
