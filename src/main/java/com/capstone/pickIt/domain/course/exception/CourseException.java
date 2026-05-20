package com.capstone.pickIt.domain.course.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class CourseException extends CustomException {

    public CourseException(CourseErrorCode errorCode) {
        super(errorCode);
    }
}