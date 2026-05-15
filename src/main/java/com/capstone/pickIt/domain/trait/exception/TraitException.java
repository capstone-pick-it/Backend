package com.capstone.pickIt.domain.trait.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class TraitException extends CustomException {

    public TraitException(TraitErrorCode errorCode) {
        super(errorCode);
    }
}