package de.digidoc.form.validation;

import de.digidoc.model.User;
import de.digidoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidatorContext;

public class UniqueEmailFormConstraintValidator extends AbstractAnyFieldValidator<UniqueUserEmailForm, Object> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueUserEmailForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object form, ConstraintValidatorContext context) {
        String emailValue = (String) getFieldValue(form, "email");
        Object idValue = getFieldValue(form, "id");
        User user = userService.findUserByEmail(emailValue);
        return (null == user || user.getId().equals(idValue));
    }
}