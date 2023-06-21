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
import java.util.List;
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
    public Flux<PnActivityReport>findAllFromPaId(String paId, String referenceMonth) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, referenceMonth));
        return this.getByFilter(conditional, PnActivityReport.INDEX_PA_REF_MONTH, null, null);
    }

    @Override
    public Mono<PnActivityReport>findByPaIdAndFileKey(String paId, String fileKey) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId, fileKey));
        return this.getByFilter(conditional, PnActivityReport.INDEX_PA_REF_MONTH, null, null).collectList().flatMap(item -> Mono.just(item.get(0)));
    }

    //@Override
    /*public Mono<List<PnActivityReport>> getAllEstimateFile(String paId) {
        QueryConditional conditional = CONDITION_EQUAL_TO.apply(keyBuild(paId));
        return this.getByFilter(conditional, null, null, null, null).collectList();
    }*/
}
