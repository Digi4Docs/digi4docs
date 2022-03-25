package de.digi4docs.form;

import de.digi4docs.form.validation.ValidPassword;
import de.digi4docs.form.validation.ValidPasswordRepetition;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ValidPasswordRepetition(message = "Das Passwort und die Passwort-Wiederholung stimmen nicht überein.")
public class ChangePasswordForm {
    @NotEmpty(message = "Bitte gib dein bisheriges Passwort an.")
    protected String oldPassword;

    @ValidPassword
    @NotEmpty(message = "Bitte gib ein neues Passwort an.")
    protected String newPassword;

    @NotEmpty(message = "Bitte wiederhole dein neues Passwort zur Sicherheit noch einmal.")
    protected String repeatPassword;
}
