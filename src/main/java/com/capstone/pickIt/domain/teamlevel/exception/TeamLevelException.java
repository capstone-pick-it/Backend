package com.capstone.pickIt.domain.teamlevel.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class TeamLevelException extends CustomException {

    public TeamLevelException(TeamLevelErrorCode errorCode) {
        super(errorCode);
    }

    public TeamLevelException(TeamLevelErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public TeamLevelException(TeamLevelErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}