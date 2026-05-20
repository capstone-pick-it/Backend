package com.capstone.pickIt.domain.course.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class ProfileException extends CustomException {

    public ProfileException(ProfileErrorCode errorCode) {
        super(errorCode);
    }
}
