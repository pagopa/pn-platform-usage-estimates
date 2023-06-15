package it.pagopa.pn.platform.middleware.db.dao.impl;

import it.pagopa.pn.platform.middleware.db.dao.ActivityReportMetaDAO;
import it.pagopa.pn.platform.middleware.db.dao.common.BaseDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnActivityReport;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

public class ActivityReportMetaDAOImpl extends BaseDAO implements ActivityReportMetaDAO {
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
}
