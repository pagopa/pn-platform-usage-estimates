package it.pagopa.pn.platform.middleware.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;

@DynamoDbBean
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PnActivityReport implements Comparable<PnActivityReport>{

    public static final String COL_REPORT_KEY = "reportKey";
    public static final String INDEX_PA_REF_MONTH = "INDEX_PA_REF_MONTH";
    public static final String COL_PA_ID = "paId" ;
    public static final String COL_REFERENCE_MONTH = "referenceMonth";
    public static final String COL_STATUS = "status";
    public static final String COL_BUCKET_NAME = "bucketName";
    public static final String COL_REPORT_ZIP_KEY = "reportZipKey";
    public static final String COL_LAST_MODIFIED_DATE = "lastModifiedDate" ;
    public static final String COL_ACTION = "action" ;

    @Getter(onMethod = @__({@DynamoDbPartitionKey, @DynamoDbSecondaryPartitionKey(indexNames = INDEX_PA_REF_MONTH), @DynamoDbAttribute(COL_PA_ID)}))
    private String paId;

    @Getter(onMethod = @__({@DynamoDbSecondarySortKey(indexNames = INDEX_PA_REF_MONTH), @DynamoDbAttribute(COL_REFERENCE_MONTH)}))
    private String referenceMonth;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_REPORT_ZIP_KEY)}))
    private String reportZipKey;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_STATUS)}))
    private String status;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_BUCKET_NAME)}))
    private String bucketName;

    @Getter(onMethod = @__({@DynamoDbSortKey, @DynamoDbAttribute(COL_REPORT_KEY)}))
    private String reportKey;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_LAST_MODIFIED_DATE)}))
    private Instant lastModifiedDate;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_ACTION)}))
    private String action;

    @Override
    public int compareTo(@NotNull PnActivityReport o) {
        return 0;
    }
}
