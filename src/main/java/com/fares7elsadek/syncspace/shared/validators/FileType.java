package com.fares7elsadek.syncspace.shared.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileType {
    String message() default "Invalid file type";
    String[] allowed();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
