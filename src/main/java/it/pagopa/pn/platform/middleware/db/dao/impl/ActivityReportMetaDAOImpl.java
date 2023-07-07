package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;


@Repository
@Slf4j
public class ActivityReportMetaDAOImpl extends BaseDAO<PnActivityReport> implements ActivityReportMetaDAO {

    public ActivityReportMetaDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                                        DynamoDbAsyncClient dynamoDbAsyncClient,
                                        AwsPropertiesConfig awsPropertiesConfig) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient,
                awsPropertiesConfig.getDynamodbActivityReportTable(), PnActivityReport.class);
    }

    @Override
    public Mono<PnActivityReport> createMetaData(PnActivityReport pnActivityReport) {
        return Mono.fromFuture(this.put(pnActivityReport).thenApply(item -> item));
    }


    @Override
    public Flux<PnActivityReport> findAllFromPaId(String paId, String referenceMonth) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, referenceMonth));
        return this.getByFilter(conditional, PnActivityReport.INDEX_PA_REF_MONTH, null, null);
    }

    @Override
    public Flux<PnActivityReport> findAllFromPaId(String paId) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, null));
        return this.getByFilter(conditional, null, null, null,5);
    }

    @Override
    public Mono<PnActivityReport> findByPaIdAndReportKey(String paId, String reportKey) {
        return Mono.fromFuture(this.get(paId, reportKey).thenApply(item -> item));
    }

    @Override
    public Flux<PnActivityReport> findAllFromPaIdAndStatus(String paId, String status) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, null));
        String filterExpression = ":statusReport = " + PnActivityReport.COL_STATUS;
        Map<String, AttributeValue> values = new HashMap<>();
        values.put(":statusReport", AttributeValue.builder().s(status).build());
        return this.getByFilter(conditional, null, values, filterExpression);
    }

}
