package ru.bia.voip.phone.model.asterisk;

public enum AsteriskExtensionType {
    PHONE("Answer"),
    QUEUE("Queue");

    private final String typeName;

    private AsteriskExtensionType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName;
    }
}
