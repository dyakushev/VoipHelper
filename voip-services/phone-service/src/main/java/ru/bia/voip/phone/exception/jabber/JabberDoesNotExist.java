package ru.bia.voip.phone.exception.jabber;

public class JabberDoesNotExist extends RuntimeException {
    public JabberDoesNotExist(String message, Throwable cause) {
        super(message, cause);
    }

    public JabberDoesNotExist() {
    }

    public JabberDoesNotExist(String message) {
        super(message);
    }
}
