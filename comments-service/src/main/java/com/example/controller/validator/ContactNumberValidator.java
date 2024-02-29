package com.example.controller.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ContactNumberValidator implements
        ConstraintValidator<PhoneNumber, String> {

    @Override
    public void initialize(PhoneNumber contactNumber) {
    }

    String patterns ="^\\+(\\d{12})";

    @Override
    public boolean isValid(String contactField,
                           ConstraintValidatorContext cxt) {
        if (contactField == null) {
            return true;
        }
        return contactField.matches(patterns);
    }
}
