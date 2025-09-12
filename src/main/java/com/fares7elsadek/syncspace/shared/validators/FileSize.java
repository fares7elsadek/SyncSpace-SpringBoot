package com.fares7elsadek.syncspace.shared.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileSize {
    String message() default "File size exceeds the allowed limit";
    long max();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
