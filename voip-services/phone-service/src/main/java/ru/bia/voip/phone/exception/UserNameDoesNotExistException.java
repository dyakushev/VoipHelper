package ru.bia.voip.phone.exception;

public class UserNameDoesNotExistException extends RuntimeException {
    public UserNameDoesNotExistException(String message) {
        super(message);
    }

    public UserNameDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
