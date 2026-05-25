package com.capstone.pickIt.domain.matching.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class MatchException extends CustomException {

    public MatchException(MatchErrorCode errorCode) {
        super(errorCode);
    }
}
