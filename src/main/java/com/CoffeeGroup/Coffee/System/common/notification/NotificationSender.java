package com.CoffeeGroup.Coffee.System.common.notification;

public interface NotificationSender {

    void send(String recipient, String subject, String html);
}
