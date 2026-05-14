package com.capstone.pickIt.api.chat.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;
import com.capstone.pickIt.global.apiPayload.response.BaseCode;

public class ChatException extends CustomException {
    public ChatException(BaseCode errorCode) {
        super(errorCode);
    }

    public ChatException(BaseCode errorCode, String message) {
        super(errorCode, message);
    }
}
