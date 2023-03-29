package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.EstimateDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnPublicAdministration;
import it.pagopa.pn.platform.utils.Const;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

import java.util.List;

@Repository
public class EstimateDAOImpl extends BaseDAO<PnEstimate> implements EstimateDAO {
    private final DynamoDbAsyncTable<PnEstimate> estimateTable;

    public EstimateDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                           DynamoDbAsyncClient dynamoDbAsyncClient,
                           AwsPropertiesConfig awsPropertiesConfig) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient,
                awsPropertiesConfig.getDynamodbEstimateTable(), PnEstimate.class);
        this.estimateTable = dynamoDbEnhancedAsyncClient.table(awsPropertiesConfig.getDynamodbEstimateTable(), TableSchema.fromBean(PnEstimate.class));
    }

    @Override
    public Mono<PnEstimate> createOrUpdate(PnEstimate estimate) {
        return Mono.fromFuture(put(estimate).thenApply(i -> estimate));
    }

    @Override
    public Mono<List<PnEstimate>> getAllEstimates(String paId) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId));
        return this.getByFilter(conditional, null, null, null, null).collectList();
    }

    @Override
    public Mono<PnEstimate> getEstimate(String paId) {
        return Mono.fromFuture(this.get(paId, null).thenApply(item -> item));
    }

    @Override
    public Mono<PnEstimate> getEstimateDetail(String paId, String referenceMonth) {
        return Mono.fromFuture(this.get(paId, referenceMonth).thenApply(item -> item));
    }


}
