package de.digi4docs.form;

import de.digi4docs.model.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
public class ImportForm {

    protected Boolean isActive;

    @NotEmpty(message = "Bitte wähle mindestens eine Rolle aus.")
    protected List<Role> roles;
}
