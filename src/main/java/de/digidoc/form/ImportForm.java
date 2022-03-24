package de.digidoc.form;

import de.digidoc.model.Role;
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
