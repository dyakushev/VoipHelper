package ru.bia.voip.phone.exception.cucm;

public class CucmLineException extends RuntimeException {
    public CucmLineException(String message, Throwable cause) {
        super(message, cause);
    }
}
