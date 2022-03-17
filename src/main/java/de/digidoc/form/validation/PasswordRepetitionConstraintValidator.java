package de.digidoc.form.validation;

import javax.validation.ConstraintValidatorContext;

public class PasswordRepetitionConstraintValidator extends AbstractAnyFieldValidator<ValidPasswordRepetition, Object> {
    @Override
    public void initialize(ValidPasswordRepetition constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object form, ConstraintValidatorContext context) {
        String newPassword = (String) getFieldValue(form, "newPassword");
        String repeatPassword = (String) getFieldValue(form, "repeatPassword");
        return (null != newPassword && null != repeatPassword && newPassword.equals(repeatPassword));
    }
}