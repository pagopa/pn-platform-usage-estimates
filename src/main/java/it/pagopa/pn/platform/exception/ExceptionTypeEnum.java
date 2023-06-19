package it.pagopa.pn.platform.exception;

import lombok.Getter;

@Getter
public enum ExceptionTypeEnum {
    ESTIMATE_NOT_EXISTED("ESTIMATE_NOT_EXISTED", "La stima non è presente nel sistema"),
    REFERENCE_MONTH_NOT_CORRECT("REFERENCE_MONTH_NOT_CORRECT", "Il mese di riferimento non è nel formato corretto"),
    PA_ID_NOT_EXIST("PA_ID_NOT_EXIST", "La pa non è presente a sistema"),
    BILLING_NOT_EXIST("BILLING_NOT_EXIST", "Fatturazione non presente a sistema"),
    OPERATION_NOT_ALLOWED("OPERATION_NOT_ALLOWED", "Operazione non consentita"),
    ESTIMATE_EXPIRED("ESTIMATE_EXPIRED", "Stima scaduta"),
    BAD_REQUEST("BAD_REQUEST", "Campi obbligatori mancanti."),
    FILE_KEY_NOT_EXISTED("FILE_KEY_NOT_EXISTED", "File key non esistente"),
    MAPPER_ERROR("MAPPER_ERROR", "Non è stato possibile mappare l'oggetto richiesto");

    private final String title;
    private final String message;


    ExceptionTypeEnum(String title, String message) {
        this.title = title;
        this.message = message;
    }
}
