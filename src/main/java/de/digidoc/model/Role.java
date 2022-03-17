package de.digidoc.model;

public enum Role {
    ADMIN("Administrator"),
    TEACHER("Lehrkraft"),
    STUDENT("Schüler*in");

    private final String displayValue;

    Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}