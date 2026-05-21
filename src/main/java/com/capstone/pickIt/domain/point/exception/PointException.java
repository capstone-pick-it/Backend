package com.capstone.pickIt.domain.point.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class PointException extends CustomException {

    public PointException(PointErrorCode errorCode) {
        super(errorCode);
    }

    public PointException(PointErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public PointException(PointErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}