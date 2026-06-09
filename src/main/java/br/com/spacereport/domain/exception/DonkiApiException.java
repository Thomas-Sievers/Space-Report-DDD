package br.com.spacereport.domain.exception;

public class DonkiApiException extends RuntimeException {

    public DonkiApiException(String message) {
        super(message);
    }

    public DonkiApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
