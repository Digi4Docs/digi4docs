package de.digi4docs.form.validation;

import de.digi4docs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailExistsValidator implements ConstraintValidator<EmailExists, String> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(EmailExists arg0) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return userService.findActiveUserByEmail(email).isPresent();
    }
}