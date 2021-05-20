package ru.bia.voip.phone.exception;


public class TwoOrMoreExtensionsOrPhonesFound extends RuntimeException {
    public TwoOrMoreExtensionsOrPhonesFound(String message) {
        super(message);
    }

    public TwoOrMoreExtensionsOrPhonesFound(String message, Throwable cause) {
        super(message, cause);
    }
}
