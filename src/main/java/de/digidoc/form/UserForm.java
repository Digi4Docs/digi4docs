package de.digidoc.form;

import de.digidoc.form.validation.UniqueUserEmail;
import de.digidoc.form.validation.ValidPassword;
import de.digidoc.model.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class UserForm {
    @NotEmpty(message = "Bitte gibt einen Vornamen an.")
    @Size(max = 255, message = "Der Vorname darf maximal 255 Zeichen lang sein.")
    protected String firstname;

    @NotEmpty(message = "Bitte gibt einen Nachnamen an.")
    @Size(max = 255, message = "Der Nachname darf maximal 255 Zeichen lang sein.")
    protected String lastname;

    @Min(value = 1, message = "Der Wert für Klasse muss mindestens 1 sein.")
    protected Integer classNumber;

    @Size(min = 0, max = 1, message = "Der Klassenbuchstabe darf maximal 1 Zeichen lang sein.")
    protected String classIdentifier;

    @Min(value = 1900, message = "Der Jahrgang darf nicht vor 1900 liegen.")
    protected Integer classYear;

    protected Boolean isActive;

    @NotEmpty(message = "Bitte wähle mindestens eine Rolle aus.")
    protected List<Role> roles;
}
