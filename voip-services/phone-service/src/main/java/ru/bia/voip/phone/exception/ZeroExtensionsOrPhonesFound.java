package ru.bia.voip.phone.exception;

public class ZeroExtensionsOrPhonesFound extends RuntimeException {
    public ZeroExtensionsOrPhonesFound(String message, Throwable cause) {
        super(message, cause);
    }

    public ZeroExtensionsOrPhonesFound(String message) {
        super(message);
    }
}
