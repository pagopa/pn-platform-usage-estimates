package it.pagopa.pn.platform.exception;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class PnRetryStorageException extends RuntimeException {
    private final BigDecimal retryAfter;

    public PnRetryStorageException(BigDecimal retryAfter) {
        super("Retry get file after.");
        this.retryAfter = retryAfter;
    }

}
