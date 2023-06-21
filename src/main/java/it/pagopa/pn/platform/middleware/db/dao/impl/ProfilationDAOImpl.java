package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.ProfilationDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.util.List;

@Repository
public class ProfilationDAOImpl  extends BaseDAO<PnProfilation> implements ProfilationDAO {

    public ProfilationDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                           DynamoDbAsyncClient dynamoDbAsyncClient,
                           AwsPropertiesConfig awsPropertiesConfig) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient,
                awsPropertiesConfig.getDynamodbProfilationTable(), PnProfilation.class);
    }

    @Override
    public Mono<PnProfilation> createOrUpdate(PnProfilation profilation) {
        return Mono.fromFuture(put(profilation).thenApply(i -> profilation));
    }

    @Override
    public Mono<List<PnProfilation>> getAllProfilations(String paId) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId));
        return this.getByFilter(conditional, null, null, null, null).collectList();
    }

    @Override
    public Mono<PnProfilation> getProfilation(String paId) {
        return Mono.fromFuture(this.get(paId, null).thenApply(item -> item));
    }

    @Override
    public Mono<PnProfilation> getProfilationDetail(String paId, String referenceYear) {
        return Mono.fromFuture(this.get(paId, referenceYear).thenApply(item -> item));
    }
}
