package it.pagopa.pn.template.middleware.db.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PnPublicAdministration {
    public static final String COL_PA_ID = "paId" ;
    public static final String COL_PA_NAME = "paName" ;
    public static final String COL_PA_NAME_INDEX = "pa-name-index";
    public static final String COL_TAX_ID = "taxId" ;
    public static final String COL_ADDRESS = "address" ;
    public static final String COL_FISCAL_CODE = "fiscalCode" ;
    public static final String COL_IPA_CODE = "ipaCode" ;
    public static final String COL_SDI_CODE = "sdiCode" ;
    public static final String COL_SPLIT_PAYMENT = "splitPayment" ;
    public static final String COL_DESCRIPTION = "description" ;
    public static final String COL_PEC = "pec" ;
    public static final String COL_MAIL_ADDRESS = "mailAddress" ;
    @Getter(onMethod = @__({@DynamoDbPartitionKey,@DynamoDbAttribute(COL_PA_ID)}))
    private String paId;
    @Getter(onMethod = @__({@DynamoDbSecondaryPartitionKey(indexNames = COL_PA_NAME_INDEX),@DynamoDbAttribute(COL_PA_NAME)}))
    private String paName;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_TAX_ID)}))
    private String taxId;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_ADDRESS)}))
    private String address;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_FISCAL_CODE)}))
    private String fiscalCode;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_IPA_CODE)}))
    private String ipaCode;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_SDI_CODE)}))
    private String sdiCode;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_SPLIT_PAYMENT)}))
    private String splitPayment;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_DESCRIPTION)}))
    private String description;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_PEC)}))
    private String pec;
    @Getter(onMethod = @__({@DynamoDbAttribute(COL_MAIL_ADDRESS)}))
    private String mailAddress;

}
