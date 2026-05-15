package com.capstone.pickIt.domain.chat.exception;

import com.capstone.pickIt.global.apiPayload.exception.CustomException;
import com.capstone.pickIt.global.apiPayload.response.BaseCode;

public class ChatException extends CustomException {
    public ChatException(BaseCode errorCode) {
        super(errorCode);
    }
}
