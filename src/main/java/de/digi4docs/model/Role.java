package de.digi4docs.model;

public enum Role {
    STUDENT("Schülerin/Schüler"),
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