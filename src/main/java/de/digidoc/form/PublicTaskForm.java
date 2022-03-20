package de.digidoc.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class PublicTaskForm {
    @NotNull(message = "Bitte wähle ein Fach aus.")
    @Min(value = 0, message = "Bitte wähle ein Fach aus.")
    protected Integer subjectId;

    @NotNull(message = "Bitte wähle eine Lehrkraft aus.")
    @Min(value = 0, message = "Bitte wähle eine Lehrkraft aus.")
    protected Integer teacherId;

    @NotEmpty(message = "Bitte gib ein Datum an.")
    protected String date;

    @NotEmpty(message = "Bitte fülle die Informationen zur Lernaufgabe bzw. zum Projekt aus.")
    protected String solution;
}
