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
public class PnProfilation implements Comparable<PnProfilation> {

    public static final String COL_PA_ID = "paId" ;
    public static final String COL_STATUS = "status";
    public static final String COL_DEADLINE_DATE = "deadlineDate";
    public static final String COL_REFERENCE_YEAR = "referenceYear";
    public static final String COL_LAST_MODIFIED_DATE = "lastModifiedDate" ;
    public static final String COL_SPLIT_PAYMENT = "splitPayment" ;
    public static final String COL_DESCRIPTION = "description" ;
    public static final String COL_MAIL_ADDRESS = "mailAddress" ;

    @Getter(onMethod = @__({@DynamoDbPartitionKey,@DynamoDbAttribute(COL_PA_ID)}))
    private String paId;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_STATUS)}))
    private String status;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_DEADLINE_DATE)}))
    private Instant deadlineDate;

    @Getter(onMethod = @__({@DynamoDbSortKey, @DynamoDbAttribute(COL_REFERENCE_YEAR)}))
    private String referenceYear;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_LAST_MODIFIED_DATE)}))
    private Instant lastModifiedDate;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_SPLIT_PAYMENT)}))
    private Boolean splitPayment;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_DESCRIPTION)}))
    private String description;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_MAIL_ADDRESS)}))
    private String mailAddress;

    @Override
    public int compareTo(@NotNull PnProfilation o) {
        return o.deadlineDate.compareTo(this.deadlineDate);
    }
}
