package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Repository
@Slf4j
public class ActivityReportMetaDAOImpl extends BaseDAO<PnActivityReport> implements ActivityReportMetaDAO {
    protected ActivityReportMetaDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                                        DynamoDbAsyncClient dynamoDbAsyncClient,
                                        AwsPropertiesConfig awsPropertiesConfig) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient,
                awsPropertiesConfig.getDynamodbEstimateTable(), PnActivityReport.class);
    }

    @Override
    public Mono<PnActivityReport> createMetaData(PnActivityReport pnActivityReport) {
        return Mono.fromFuture(this.put(pnActivityReport).thenApply(item -> item));
    }

    @Override
    public Mono<PnActivityReport> getCSVName(String paId, String fileKey) {

        QueryConditional conditionalKey = CONDITION_EQUAL_TO.apply(keyBuild(paId, fileKey));
        return this.getByFilter(conditionalKey, null, null, null, null).collectList()
                .flatMap(item -> {
                    if (item == null || item.isEmpty()){
                        return Mono.empty();
                    }
                    return Mono.just(item.get(0));
                });
    }
}
