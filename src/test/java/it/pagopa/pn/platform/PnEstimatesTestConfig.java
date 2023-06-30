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
        stringBuilder.append("clientNationalRegistriesBasepath=");
        stringBuilder.append(clientExternalRegistriesBasepath);
        stringBuilder.append(", ");
        stringBuilder.append("xPagopaExtchCxId=");
        stringBuilder.append(xPagopaExtchCxId);
        stringBuilder.append(")");
        String toTest = stringBuilder.toString();
        Assertions.assertEquals(toTest, pnPlatformConfig.toString());
    }

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
