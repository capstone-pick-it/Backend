package com.capstone.pickIt.api.chat.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatUserNotificationEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ChatUserNotificationEvent event) {
        String destination = "/queue/notifications/" + event.receiverId();

        log.info(
                "Sending chat notification: receiverId={}, destination={}",
                event.receiverId(),
                destination
        );

        messagingTemplate.convertAndSend(
                destination,
                event.payload()
        );
    }
}
