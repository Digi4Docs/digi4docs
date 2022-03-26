package de.digi4docs.model;

public enum Role {
    ADMIN("Administrator"),
    TEACHER("Lehrkraft"),
    COURSES("Kurs-Organisation"),
    USERS("Benutzerverwaltung"),
    STUDENT("Schüler*in");

    private final String displayValue;

    Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}