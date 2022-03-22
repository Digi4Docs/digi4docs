package de.digidoc.form;

import de.digidoc.form.validation.ValidPassword;
import de.digidoc.form.validation.ValidPasswordRepetition;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ValidPasswordRepetition(message = "Das Passwort und die Passwort-Wiederholung stimmen nicht überein.")
public class NewPasswordForm {
    @ValidPassword
    @NotEmpty(message = "Bitte gib ein neues Passwort an.")
    protected String newPassword;

    @NotEmpty(message = "Bitte wiederhole dein neues Passwort zur Sicherheit noch einmal.")
    protected String repeatPassword;
}
