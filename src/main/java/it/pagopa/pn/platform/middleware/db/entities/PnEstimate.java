package it.pagopa.pn.platform.middleware.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class PnEstimate {

    public static final String COL_PA_ID = "paId" ;

    public static final String COL_STATUS = "status";

    public static final String COL_DEADLINE_DATE = "deadlineDate";

    public static final String COL_REFERENCE_MONTH = "referenceMonth";

    public static final String COL_TOTAL_DIGITAL_NOTIF = "totalDigitalNotif";

    public static final String COL_TOTAL_PAPER_890_NOTIF = "totalPaper890Notif" ;

    public static final String COL_TOTAL_PAPER_NATIONAL_NOTIF = "totalPaperNationalNotif" ;

    public static final String COL_TOTAL_PAPER_INTERNATIONAL_NOTIF = "totalPaperInternationalNotif" ;

    public static final String COL_LAST_MODIFIED_TIMESTAMP = "lastModifiedTimestamp" ;

    @Getter(onMethod = @__({@DynamoDbPartitionKey,@DynamoDbAttribute(COL_PA_ID)}))
    private String paId;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_STATUS)}))
    private Instant status;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_DEADLINE_DATE)}))
    private Instant deadlineDate;

    @Getter(onMethod = @__({@DynamoDbSortKey, @DynamoDbAttribute(COL_REFERENCE_MONTH)}))
    private String referenceMonth;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_TOTAL_DIGITAL_NOTIF)}))
    private Integer totalDigitalNotif;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_TOTAL_PAPER_890_NOTIF)}))
    private Integer totalPaper890Notif;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_TOTAL_PAPER_NATIONAL_NOTIF)}))
    private Integer totalPaperNationalNotif;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_TOTAL_PAPER_INTERNATIONAL_NOTIF)}))
    private Integer totalPaperInternationalNotif;

    @Getter(onMethod = @__({@DynamoDbAttribute(COL_LAST_MODIFIED_TIMESTAMP)}))
    private Instant lastModifiedTimestamp;


}
