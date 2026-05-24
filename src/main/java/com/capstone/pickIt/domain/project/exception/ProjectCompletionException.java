package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class ProjectCompletionException extends CustomException {

    public ProjectCompletionException(ProjectCompletionErrorCode errorCode) {
        super(errorCode);
    }

    public ProjectCompletionException(ProjectCompletionErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}