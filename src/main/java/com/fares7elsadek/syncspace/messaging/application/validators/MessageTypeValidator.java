package com.fares7elsadek.syncspace.messaging.application.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class MessageTypeValidator implements ConstraintValidator<ValidMessageType, String> {

    private final String[] validTypes = {"TEXT", "IMAGE", "FILE", "VOICE", "VIDEO"};
    @Override
    public boolean isValid(String messageType, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.stream(validTypes).anyMatch(messageType::equals);
    }
}
