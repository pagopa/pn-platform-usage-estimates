package it.pagopa.pn.platform.msclient.impl;

import it.pagopa.pn.platform.config.PnPlatformConfig;
import it.pagopa.pn.platform.msclient.DataVaultEncryptionClient;
import it.pagopa.pn.platform.exception.ExceptionTypeEnum;
import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.ApiClient;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.api.RecipientsApi;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.dto.BaseRecipientDtoDto;
import it.pagopa.pn.platform.generated.openapi.msclient.pndatavault.v1.dto.RecipientTypeDto;
import it.pagopa.pn.platform.msclient.common.BaseClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component("dataVaultEncryption")
public class DataVaultEncryptionClientImpl extends BaseClient implements DataVaultEncryptionClient {

    private RecipientsApi recipientsApi;

    private final PnPlatformConfig pnPlatformConfig;


    public DataVaultEncryptionClientImpl(PnPlatformConfig pnPlatformConfig) {
        this.pnPlatformConfig = pnPlatformConfig;
    }

    @PostConstruct
    public void init(){
        ApiClient apiClient = new ApiClient(super.initWebClient(ApiClient.buildWebClientBuilder()));
        apiClient.setBasePath(pnPlatformConfig.getClientDataVaultBasepath());
        this.recipientsApi = new RecipientsApi(apiClient);
    }

    @Override
    public String encode(String fiscalCode, String type) {
        return this.recipientsApi.ensureRecipientByExternalId(
                (StringUtils.equalsIgnoreCase(type, RecipientTypeDto.PF.getValue()) ? RecipientTypeDto.PF: RecipientTypeDto.PG), fiscalCode)
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(25))
                                .filter(throwable ->throwable instanceof TimeoutException || throwable instanceof ConnectException)
                ).map(item -> item)
                .onErrorResume(ex -> Mono.error(new PnGenericException(ExceptionTypeEnum.DATA_VAULT_ENCRYPTION_ERROR, ExceptionTypeEnum.DATA_VAULT_ENCRYPTION_ERROR.getMessage())))
                .block();
    }

    @Override
    public String decode(String data) {
        List<String> toDecode = new ArrayList<>();
        toDecode.add(data);
        return this.recipientsApi.getRecipientDenominationByInternalId(toDecode)
                .retryWhen(
                        Retry.backoff(2, Duration.ofMillis(25))
                                .filter(throwable ->throwable instanceof TimeoutException || throwable instanceof ConnectException)
                )
                .map(BaseRecipientDtoDto::getTaxId)
                .onErrorResume(ex -> {
                    log.error("Error {}", ex.getMessage());
                    return Mono.error(new PnGenericException(ExceptionTypeEnum.DATA_VAULT_DECRYPTION_ERROR, ExceptionTypeEnum.DATA_VAULT_DECRYPTION_ERROR.getMessage()));
                })
                .blockFirst();
    }
}