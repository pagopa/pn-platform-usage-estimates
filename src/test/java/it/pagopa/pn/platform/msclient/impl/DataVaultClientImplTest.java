package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.config.BaseTest;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.api.RecipientsApi;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.dto.BaseRecipientDtoDto;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.dto.RecipientTypeDto;
import it.pagopa.pn.platform.msclient.DataVaultClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataVaultClientImplTest extends BaseTest {

    @MockBean
    private RecipientsApi recipientsApi;
    @Autowired
    private DataVaultClient dataVaultClient;

    private BaseRecipientDtoDto baseRecipientDtoDto = new BaseRecipientDtoDto();

    @BeforeEach
    void init(){
        baseRecipientDtoDto.setRecipientType(RecipientTypeDto.PF);
        baseRecipientDtoDto.setDenomination("denomination");
        baseRecipientDtoDto.setInternalId("internalId");
        baseRecipientDtoDto.setTaxId("taxId");
    }

    @Test
    void decode(){

        Mockito.when(this.recipientsApi.getRecipientDenominationByInternalId(Mockito.any())).thenReturn(Flux.just(baseRecipientDtoDto));
        String result = this.dataVaultClient.decode("data");
        Assertions.assertNotNull(result);

    }
}
