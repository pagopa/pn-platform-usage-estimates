package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.BillingDAO;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnBilling;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Repository
public class BillingDAOImpl  extends BaseDAO<PnBilling> implements BillingDAO {
    public BillingDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                           DynamoDbAsyncClient dynamoDbAsyncClient,
                           AwsPropertiesConfig awsPropertiesConfig) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient,
                awsPropertiesConfig.getDynamodbEstimateTable(), PnBilling.class);
    }

    @Override
    public Mono<PnBilling> createOrUpdate(PnBilling data) {
        return Mono.fromFuture(put(data).thenApply(i -> data));
    }

    @Override
    public Mono<PnBilling> getProfilationDetail(String paId) {
        return Mono.fromFuture(this.get(paId, null).thenApply(item -> item));
    }

    @Override
    public Mono<PnBilling> getProfilationAndBillingDetail(String paId, String referenceYear)  {
        return null;
    }
}
