package it.pagopa.pn.platform.middleware.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;

@DynamoDbBean
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PnActivityReport implements Comparable<PnActivityReport>{

    public static final String COL_FILE_KEY = "fileKey";
    public static final String COL_PA_ID = "paId" ;
    public static final String COL_STATUS = "status";
    public static final String COL_BUCKET_NAME = "bucketName";
    public static final String COL_FILE_ZIP_KEY = "fileZipKey";

    @Getter(onMethod = @__({@DynamoDbPartitionKey,@DynamoDbAttribute(COL_PA_ID)}))
    private String paId;

    @Getter(onMethod = @__({@DynamoDbSortKey, @DynamoDbAttribute(COL_FILE_KEY)}))
    private String fileKey;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_STATUS)}))
    private String status;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_BUCKET_NAME)}))
    private String bucketName;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_FILE_ZIP_KEY)}))
    private String fileZipKey;

    @Override
    public int compareTo(@NotNull PnActivityReport o) {
        return 0;
    }
}
