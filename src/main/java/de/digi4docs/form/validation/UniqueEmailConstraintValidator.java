package de.digi4docs.form.validation;

import de.digi4docs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailConstraintValidator implements ConstraintValidator<UniqueUserEmail, String> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(UniqueUserEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return (null == userService.findUserByEmail(email));
    }
}