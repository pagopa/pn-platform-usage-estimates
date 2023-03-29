package it.pagopa.pn.platform.exception;

import lombok.Getter;

@Getter
public enum ExceptionTypeEnum {
    ESTIMATE_NOT_EXISTED("ESTIMATE_NOT_EXISTED", "La stima non è presente nel sistema");

    private final String title;
    private final String message;


    ExceptionTypeEnum(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
