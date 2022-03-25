package de.digi4docs.form;

import de.digi4docs.form.validation.EmailExists;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class PasswordForgottenForm {
    @NotEmpty(message = "Bitte gibt eine E-Mail an.")
    @Size(max = 255, message = "Die E-Mail darf maximal 255 Zeichen lang sein.")
    @Email(message = "Bitte gib eine gültige E-Mail an.")
    @EmailExists(message = "Die angegebene E-Mail befindet sich nicht im System oder ist zur Zeit gesperrt.")
    protected String email;
}
