package it.pagopa.pn.platform.exception;

import lombok.Getter;

@Getter
public enum ExceptionTypeEnum {
    ESTIMATE_NOT_EXISTED("ESTIMATE_NOT_EXISTED", "La stima non è presente nel sistema"),
    PA_ID_NOT_EXIST("PA_ID_NOT_EXIST", "La pa non è presente a sistema"),
    BILLING_NOT_EXIST("BILLING_NOT_EXIST", "Fatturazione non presente a sistema");

    private final String title;
    private final String message;


    ExceptionTypeEnum(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
