package com.fares7elsadek.syncspace.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileSizeValidator implements ConstraintValidator<FileSize, MultipartFile> {
    private long max;
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext constraintValidatorContext) {
        return file != null && file.getSize() <= max;
    }
    @Override
    public void initialize(FileSize constraintAnnotation) {
        this.max = constraintAnnotation.max();
    }
}
