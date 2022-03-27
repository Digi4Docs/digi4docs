package de.digi4docs.form.validation;

import de.digi4docs.model.User;
import de.digi4docs.service.UserService;
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
        return (null == user || user.getId()
                                    .equals(idValue));
    }
}