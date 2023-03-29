package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.PublicAdministrationDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnPublicAdministration;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;


@Repository
public class PublicAdministrationDAOImpl extends BaseDAO<PnPublicAdministration> implements PublicAdministrationDAO {

    private final DynamoDbAsyncTable<PnPublicAdministration> publicAdministrationTable;

    public PublicAdministrationDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                           DynamoDbAsyncClient dynamoDbAsyncClient,
                           AwsPropertiesConfig awsPropertiesConfig) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient,
                awsPropertiesConfig.getDynamodbEstimateTable(), PnPublicAdministration.class);
        this.publicAdministrationTable = dynamoDbEnhancedAsyncClient.table(awsPropertiesConfig.getDynamodbPublicAdministrationTable(), TableSchema.fromBean(PnPublicAdministration.class));
    }

    @Override
    public Mono<PnPublicAdministration> getPaDetail(String paId, String referenceMonth) {
        return Mono.fromFuture(this.get(paId, referenceMonth).thenApply(item -> item));
    }
}
