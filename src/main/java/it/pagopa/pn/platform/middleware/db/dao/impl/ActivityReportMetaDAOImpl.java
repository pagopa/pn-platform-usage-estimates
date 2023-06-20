package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityReportMetaDAOImpl extends BaseDAO<PnActivityReport> implements ActivityReportMetaDAO {
    protected ActivityReportMetaDAOImpl(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                                        DynamoDbAsyncClient dynamoDbAsyncClient,
                                        String tableName,
                                        Class aClass) {
        super(dynamoDbEnhancedAsyncClient, dynamoDbAsyncClient, tableName, aClass);
    }

    @Override
    public Mono<PnActivityReport> createMetaData(PnActivityReport pnActivityReport) {
        return Mono.fromFuture(this.put(pnActivityReport).thenApply(item -> item));
    }


    @Override
    public Flux<PnActivityReport>findAllFromPaId(String paId, String referenceMonth) {
        //TODO inserire refMonth in keyBuild
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId));
        String filter = "";
        Map<String, AttributeValue> values = new HashMap<>();
        if (StringUtils.isNotBlank(referenceMonth)) {
            filter += ":referenceMonth=" + PnActivityReport.COL_REFERENCE_MONTH;
            values.put(":referenceMonth", AttributeValue.builder().s(referenceMonth).build());
        }
        return this.getByFilter(conditional, PnActivityReport.INDEX_PA_REF_MONTH, values, filter);
    }

    //@Override
    /*public Mono<List<PnActivityReport>> getAllEstimateFile(String paId) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId));
        return this.getByFilter(conditional, null, null, null, null).collectList();
    }*/
}
