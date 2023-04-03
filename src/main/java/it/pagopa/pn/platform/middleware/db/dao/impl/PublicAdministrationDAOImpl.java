package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.PublicAdministrationDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnPublicAdministration;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;


@Repository
public class PublicAdministrationDAOImpl extends BaseDAO<PnPublicAdministration> implements PublicAdministrationDAO {

    public PublicAdministrationDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                           DynamoDbAsyncClient dynamoDbAsyncClient,
                           AwsPropertiesConfig awsPropertiesConfig) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient,
                awsPropertiesConfig.getDynamodbPublicAdministrationTable(), PnPublicAdministration.class);
    }

    @Override
    public Mono<PnPublicAdministration> createOrUpdate(PnPublicAdministration pnPublicAdministration) {
        return Mono.fromFuture(this.put(pnPublicAdministration).thenApply(item -> pnPublicAdministration));
    }

    @Override
    public Mono<PnPublicAdministration> getPaDetail(String paId, String referenceMonth) {
        return Mono.fromFuture(this.get(paId, referenceMonth).thenApply(item -> item));
    }
}
