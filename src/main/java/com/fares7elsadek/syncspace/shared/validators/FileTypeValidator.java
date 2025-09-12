package com.fares7elsadek.syncspace.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileTypeValidator implements ConstraintValidator<FileType, MultipartFile> {

    private String[] allowedTypes;
    @Override
    public void initialize(FileType constraintAnnotation) {
        this.allowedTypes = constraintAnnotation.allowed();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        return file != null && Arrays.asList(allowedTypes).contains(file.getContentType());
    }
}
