package com.capstone.pickIt.domain.chat.repository;

public interface UnreadCountByUserProjection {
    Long getUserId();
    Long getUnreadCount();
}
