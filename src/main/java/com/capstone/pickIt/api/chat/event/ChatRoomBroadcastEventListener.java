package com.capstone.pickIt.api.chat.event;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatRoomBroadcastEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ChatRoomBroadcastEvent event) {
        messagingTemplate.convertAndSend(
                "/topic/chatrooms/" + event.chatRoomId(),
                event.payload()
        );
    }
}
