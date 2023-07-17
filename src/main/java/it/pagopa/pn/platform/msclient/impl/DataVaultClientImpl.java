package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.api.RecipientsApi;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.dto.BaseRecipientDtoDto;
import it.pagopa.pn.platform.msclient.DataVaultClient;
import it.pagopa.pn.platform.msclient.common.BaseClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class DataVaultClientImpl extends BaseClient implements DataVaultClient {

    @Autowired
    private RecipientsApi recipientsApi;


    @Override
    public String decode(String data) {
        List<String> toDecode = new ArrayList<>();
        toDecode.add(data);
        return this.recipientsApi.getRecipientDenominationByInternalId(toDecode)
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(25))
                                .filter(throwable ->throwable instanceof TimeoutException || throwable instanceof ConnectException)
                )
                .mapNotNull(BaseRecipientDtoDto::getTaxId)
                .onErrorResume(ex -> {
                    log.error("Error {}", ex.getMessage());
                    return Mono.error(new PnGenericException(ExceptionTypeEnum.DATA_VAULT_DECRYPTION_ERROR, ExceptionTypeEnum.DATA_VAULT_DECRYPTION_ERROR.getMessage()));
                })
                .blockFirst();
    }
}