package de.digidoc.form.validation;

import javax.validation.ConstraintValidator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class AbstractAnyFieldValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {
    protected Object getFieldValue(Object object, String fieldName) {
        try {
            Class<?> clazz = object.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return "";
        }
    }
}
