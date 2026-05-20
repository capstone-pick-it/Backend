package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class ChecklistException extends CustomException {

    public ChecklistException(ChecklistErrorCode errorCode) {
        super(errorCode);
    }

    public ChecklistException(ChecklistErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public ChecklistException(
            ChecklistErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        super(errorCode, message, cause);
    }
}