package com.marketplace.notification.channels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDispatchChannel2 {

    public boolean dispatchSms(String phoneNumber, String messageText) {
        log.info("Dispatching SMS alert via Gateway #2 to {}: {}", phoneNumber, messageText);
        return true;
    }

    public boolean dispatchPushNotification(String deviceToken, String title, String body) {
        log.info("Dispatching Push Notification via Provider #2 [token={}]: {}", deviceToken, title);
        return true;
    }
}
