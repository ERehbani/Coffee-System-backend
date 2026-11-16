package com.CoffeeGroup.Coffee.System.common.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ResendNotificationSender implements NotificationSender {

    private final Resend resend;
    private final String from;

    public ResendNotificationSender(@Value("${resend.apikey}") String apiKey, @Value("${app.mail.from}") String from) {
        this.resend = new Resend(apiKey.replaceAll("^\"|\"$", ""));
        this.from = from;
    }

    @Override
    public void send(String recipient, String subject, String html) {
        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(from)
                .to(recipient)
                .subject(subject)
                .html(html)
                .build();
        try {
            CreateEmailResponse response = resend.emails().send(email);
            log.info("Email '{}' sent with id {}", subject, response.getId());
        } catch (ResendException e) {
            log.error("Could not send the email '{}'", subject, e);
        }
    }
}
