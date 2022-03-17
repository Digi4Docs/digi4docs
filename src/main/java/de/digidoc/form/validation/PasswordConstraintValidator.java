package de.digidoc.form.validation;

import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (null == password || password.isEmpty()) {
            return true;
        }

        Properties props = new Properties();
        props.put("INSUFFICIENT_UPPERCASE", "Das Password muss mindestens %1$s Großbuchstaben enthalten.");
        props.put("INSUFFICIENT_LOWERCASE", "Das Password muss mindestens %1$s Kleinbuchstaben enthalten.");
        props.put("INSUFFICIENT_DIGIT", "Das Password muss mindestens %1$s Zahl enthalten.");
        props.put("INSUFFICIENT_SPECIAL", "Das Password muss mindestens %1$s Sonderzeichen enthalten.");
        props.put("TOO_SHORT", "Das Password muss mindestens %1$s Zeichen lang sein.");
        props.put("TOO_LONG", "Das Password darf maximal %2$s Zeichen lang sein.");
        MessageResolver resolver = new PropertiesMessageResolver(props);

        PasswordValidator validator = new PasswordValidator(resolver, Arrays.asList(
                new LengthRule(8, 255),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1)));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }

        List<String> messages = validator.getMessages(result);
        String messageTemplate = String.join(" ", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}