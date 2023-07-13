package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.config.AwsPropertiesConfig;
import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    public Flux<PnActivityReport> findAllFromPaId(String paId, String referenceMonth, String status) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, referenceMonth));
        String filter = null;
        Map<String, AttributeValue> attributes = null;
        if (StringUtils.isNotBlank(status)){
            filter = PnActivityReport.COL_STATUS.concat("= :status");
            attributes = new HashMap<>();
            attributes.put(":status", AttributeValue.builder().s(status).build());
        }
        return this.getByFilter(conditional, PnActivityReport.INDEX_PA_REF_MONTH, attributes, filter);
    }


    @Override
    public Flux<PnActivityReport> findAllFromPaId(String paId) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, null));
        return this.getByFilter(conditional, null, null, null);
    }

    @Override
    public Mono<PnActivityReport> findByPaIdAndReportKey(String paId, String reportKey) {
        return Mono.fromFuture(this.get(paId, reportKey).thenApply(item -> item));
    }

    @Override
    public Flux<PnActivityReport> findAllFromPaIdAndStatus(String paId, String status) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, null));
        String filter = PnActivityReport.COL_STATUS.concat("= :valueStatus");
        Map<String, AttributeValue> attributes = new HashMap<>();
        attributes.put(":valueStatus", AttributeValue.builder().s(status).build());
        return this.getByFilter(conditional, null, attributes, filter);
    }

}
