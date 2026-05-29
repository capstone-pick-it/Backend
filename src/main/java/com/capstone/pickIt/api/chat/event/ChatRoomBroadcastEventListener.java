package com.capstone.pickIt.api.chat.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRoomBroadcastEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ChatRoomBroadcastEvent event) {

        log.info(
                "Broadcasting chat room event: chatRoomId={}, destination=/topic/chatrooms/{}",
                event.chatRoomId(),
                event.chatRoomId()
        );

        // 채팅방 구독자에게 메시지 브로드캐스트
        messagingTemplate.convertAndSend(
                "/topic/chatrooms/" + event.chatRoomId(),
                event.payload()
        );
    }
}
