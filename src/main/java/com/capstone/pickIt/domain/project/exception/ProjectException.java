package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class ProjectException extends CustomException {

    public ProjectException(ProjectErrorCode errorCode) {
        super(errorCode);
    }

    public ProjectException(ProjectErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}