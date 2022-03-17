package de.digidoc.form;

import de.digidoc.form.validation.UniqueUserEmailForm;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@UniqueUserEmailForm(message = "Diese E-Mail existiert bereits im System. Wähle eine andere E-Mail.")
public class ProfileForm {
    protected Integer id;

    @NotEmpty(message = "Bitte gibt eine E-Mail an.")
    @Size(max = 255, message = "Die E-Mail darf maximal 255 Zeichen lang sein.")
    @Email(message = "Bitte gib eine gültige E-Mail an.")
    protected String email;

    @NotEmpty(message = "Bitte gibt einen Vornamen an.")
    @Size(max = 255, message = "Der Vorname darf maximal 255 Zeichen lang sein.")
    protected String firstname;

    @NotEmpty(message = "Bitte gibt einen Nachnamen an.")
    @Size(max = 255, message = "Der Nachname darf maximal 255 Zeichen lang sein.")
    protected String lastname;

    @Min(value = 1, message = "Der Wert für Klasse muss mindestens 1 sein.")
    protected Integer classNumber;
}
