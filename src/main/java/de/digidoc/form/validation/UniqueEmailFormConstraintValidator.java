package de.digidoc.form.validation;

import de.digidoc.form.UserEditForm;
import de.digidoc.model.User;
import de.digidoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailFormConstraintValidator implements ConstraintValidator<UniqueUserEmailForm, UserEditForm> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueUserEmailForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserEditForm form, ConstraintValidatorContext context) {
        String email = form.getEmail();
        User user = userService.findUserByEmail(email);
        return (null == user || user.getId().equals(form.getId()));
    }
}