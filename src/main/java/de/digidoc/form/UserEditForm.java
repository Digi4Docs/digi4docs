package de.digidoc.form;

import de.digidoc.form.validation.UniqueUserEmail;
import de.digidoc.form.validation.UniqueUserEmailForm;
import de.digidoc.form.validation.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@UniqueUserEmailForm(message = "Diese E-Mail existiert bereits im System. Wähle eine andere E-Mail.")
public class UserEditForm extends UserForm {
    protected Integer id;

    @ValidPassword
    protected String password;

    @NotEmpty(message = "Bitte gibt eine E-Mail an.")
    @Size(max = 255, message = "Die E-Mail darf maximal 255 Zeichen lang sein.")
    @Email(message = "Bitte gib eine gültige E-Mail an.")
    protected String email;
}
