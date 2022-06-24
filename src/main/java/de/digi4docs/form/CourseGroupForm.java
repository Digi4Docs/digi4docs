package de.digi4docs.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CourseGroupForm {
    protected Integer id;

    @NotEmpty(message = "Bitte gib eine Bezeichnung für die Gruppe an.")
    @Size(max = 255, message = "Die Bezeichnung darf maximal 255 Zeichen lang sein.")
    protected String name;

    protected Boolean isActive;
}
