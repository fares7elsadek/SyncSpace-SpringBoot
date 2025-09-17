package com.fares7elsadek.syncspace.messaging.application.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MessageTypeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMessageType {
    String message() default " Message Type must be ('TEXT','IMAGE','FILE','VOICE','VIDEO'). ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
