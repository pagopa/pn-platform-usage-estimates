package it.pagopa.pn.platform;

import it.pagopa.pn.platform.config.PnPlatformConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PnEstimatesTestConfig {

    private String clientExternalRegistriesBasepath;
    private String xPagopaExtchCxId;


    @BeforeEach
    void setUp(){
        this.initialize();
    }

    @Test
    void setGetTest() {
        PnPlatformConfig pnPlatformConfig = initPnPlatformConfig();
        Assertions.assertNotNull(pnPlatformConfig);
    }

    @Test
    void toStringTest() {
        PnPlatformConfig pnPlatformConfig = initPnPlatformConfig();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(pnPlatformConfig.getClass().getSimpleName());
        stringBuilder.append("(");
        stringBuilder.append("clientSafeStorageBasepath=");
        stringBuilder.append(pnPlatformConfig.getClientSafeStorageBasepath());
        stringBuilder.append(", ");
        stringBuilder.append("clientExternalRegistriesBasepath=");
        stringBuilder.append(pnPlatformConfig.getClientExternalRegistriesBasepath());
        stringBuilder.append(", ");
        stringBuilder.append("xPagopaExtchCxId=");
        stringBuilder.append(pnPlatformConfig.getXPagopaExtchCxId());
        stringBuilder.append(", ");
        stringBuilder.append("safeStorageCxId=");
        stringBuilder.append(pnPlatformConfig.getSafeStorageCxId());
        stringBuilder.append(", ");
        stringBuilder.append("clientDataVaultBasepath=");
        stringBuilder.append(pnPlatformConfig.getClientDataVaultBasepath());
        stringBuilder.append(", ");
        stringBuilder.append("datalakeBucketName=");
        stringBuilder.append(pnPlatformConfig.getDatalakeBucketName());
        stringBuilder.append(", ");
        stringBuilder.append("datalakeReportKey=");
        stringBuilder.append(pnPlatformConfig.getDatalakeReportKey());
        stringBuilder.append(", ");
        stringBuilder.append("jobQueueName=");
        stringBuilder.append(pnPlatformConfig.getJobQueueName());
        stringBuilder.append(", ");
        stringBuilder.append("jobDefinitionName=");
        stringBuilder.append(pnPlatformConfig.getJobDefinitionName());
        stringBuilder.append(", ");
        stringBuilder.append("attemptDataVault=");
        stringBuilder.append(pnPlatformConfig.getAttemptDataVault());
        stringBuilder.append(")");
        String toTest = stringBuilder.toString();
        Assertions.assertEquals(toTest, pnPlatformConfig.toString());
    }

    //PnPlatformConfig(clientNationalRegistriesBasepath=clientNationalRegistriesBasepath, xPagopaExtchCxId=xPagopaExtchCxId)

    private PnPlatformConfig initPnPlatformConfig() {
        PnPlatformConfig pnPlatformConfig = new PnPlatformConfig();
        pnPlatformConfig.setClientExternalRegistriesBasepath(clientExternalRegistriesBasepath);
        pnPlatformConfig.setXPagopaExtchCxId(xPagopaExtchCxId);
        return pnPlatformConfig;
    }

    private void initialize() {
        clientExternalRegistriesBasepath = "clientNationalRegistriesBasepath";
        xPagopaExtchCxId = "xPagopaExtchCxId";
    }
}
