package ru.bia.voip.phone.exception.jabber;

public class JabberExtensionException extends RuntimeException {
    public JabberExtensionException(String message) {
        super(message);
    }

    public JabberExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

}
