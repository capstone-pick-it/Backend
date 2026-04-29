package com.capstone.pickIt.domain.user.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class UserException extends CustomException {

    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}