package de.digi4docs.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TextTemplateForm {

    @NotEmpty(message = "Bitte gibt eine Bezeichnung an.")
    @Size(max = 255, message = "Die Bezeichnung darf maximal 255 Zeichen lang sein.")
    protected String title;

    protected String text;
}
