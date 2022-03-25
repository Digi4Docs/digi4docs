package de.digi4docs.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class SubjectForm {
    protected Integer id;

    @NotEmpty(message = "Bitte gibt eine Bezeichnung für das Fach an.")
    @Size(max = 255, message = "Die Bezeichung darf maximal 255 Zeichen lang sein.")
    protected String name;

    protected Boolean isActive;

    protected List<Integer> teachers;
}
