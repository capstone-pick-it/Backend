package com.capstone.pickIt.domain.project.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;

public class PeerReviewException extends CustomException {

    public PeerReviewException(PeerReviewErrorCode errorCode) {
        super(errorCode);
    }

    public PeerReviewException(
            PeerReviewErrorCode errorCode,
            String message
    ) {
        super(errorCode, message);
    }
}