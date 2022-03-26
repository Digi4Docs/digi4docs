package de.digi4docs.model;

public enum Role {
    STUDENT("Schüler*in"),
    TEACHER("Lehrkraft"),
    ADMIN("Administrator"),
    COURSES("Kurs-Organisation"),
    USERS("Benutzerverwaltung");

    private final String displayValue;

    Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}