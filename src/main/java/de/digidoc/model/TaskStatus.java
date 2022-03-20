package de.digidoc.model;

public enum TaskStatus {
    OPEN("offen"),
    TRANSMITTED("abgegeben"),
    REJECTED("zurückgegeben"),
    DONE("erledigt");

    private final String displayValue;

    TaskStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
