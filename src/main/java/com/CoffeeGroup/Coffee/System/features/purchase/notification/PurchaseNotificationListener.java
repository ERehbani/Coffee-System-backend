package com.CoffeeGroup.Coffee.System.features.purchase.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.CoffeeGroup.Coffee.System.common.notification.NotificationSender;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PurchaseNotificationListener {

    private final NotificationSender notificationSender;
    private final PurchaseEmailTemplate purchaseEmailTemplate;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseConfirmed(PurchaseConfirmedEvent event) {
        PurchaseReceipt receipt = event.receipt();
        notificationSender.send(receipt.recipient(), "Tu compra fue confirmada", purchaseEmailTemplate.confirmed(receipt));
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPurchaseCancelled(PurchaseCancelledEvent event) {
        PurchaseReceipt receipt = event.receipt();
        notificationSender.send(receipt.recipient(), "Tu compra fue cancelada",
                purchaseEmailTemplate.cancelled(receipt, event.revokedPoints()));
    }
}
