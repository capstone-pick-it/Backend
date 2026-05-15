package com.capstone.pickIt.global.validation.validator;

import com.capstone.pickIt.global.validation.annotation.NoBadWords;
import com.vane.badwordfiltering.BadWordFiltering;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoBadWordsValidator implements ConstraintValidator<NoBadWords, String> {

    private final BadWordFiltering badWordFiltering = new BadWordFiltering();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return !badWordFiltering.check(value);
    }
}