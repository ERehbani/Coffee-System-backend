package com.CoffeeGroup.Coffee.System.features.payment.mercadopago;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.common.exceptions.PaymentGatewayException;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.net.MPSearchRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import lombok.extern.slf4j.Slf4j;

/**
 * Thin wrapper around the Mercado Pago SDK (Checkout Pro).
 * It knows nothing about points, carts or redemptions: PurchaseService decides what a payment means.
 */
@Slf4j
@Service
public class MercadoPagoService {

    public static final String STATUS_APPROVED = "approved";
    public static final String STATUS_REFUNDED = "refunded";
    public static final String STATUS_CHARGED_BACK = "charged_back";

    private final String accessToken;
    private final String webhookSecret;
    private final String notificationUrl;
    private final String frontendUrl;

    public MercadoPagoService(
            @Value("${mercadopago.access-token:}") String accessToken,
            @Value("${mercadopago.webhook-secret:}") String webhookSecret,
            @Value("${mercadopago.notification-url:}") String notificationUrl,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.accessToken = accessToken;
        this.webhookSecret = webhookSecret;
        this.notificationUrl = notificationUrl;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");

        if (accessToken.isBlank()) {
            log.warn("MP_ACCESS_TOKEN is not set: purchases paid with Mercado Pago will fail.");
        }
        if (webhookSecret.isBlank()) {
            log.warn("MP_WEBHOOK_SECRET is not set: webhook signatures will not be checked.");
        }
    }

    public record Checkout(String preferenceId, String paymentUrl) {
    }

    /**
     * Creates a Checkout Pro preference for the purchase. The client is sent to {@code paymentUrl} to pay.
     * A single line item with the final price is used so discounts and free products always add up.
     */
    public Checkout createCheckout(PurchaseEntity purchase) {
        String cafeName = purchase.getCafe() != null ? purchase.getCafe().getName() : "Coffee System";

        PreferenceItemRequest item = PreferenceItemRequest.builder()
                .id(purchase.getExternalId().toString())
                .title("Compra en " + cafeName)
                .quantity(1)
                .unitPrice(purchase.getPrice().setScale(2, RoundingMode.HALF_UP))
                .build();

        String checkoutUrl = frontendUrl + "/checkout";
        PreferenceRequest.PreferenceRequestBuilder request = PreferenceRequest.builder()
                .items(List.of(item))
                .externalReference(purchase.getExternalId().toString())
                .backUrls(PreferenceBackUrlsRequest.builder()
                        .success(checkoutUrl + "/success")
                        .pending(checkoutUrl + "/pending")
                        .failure(checkoutUrl + "/failure")
                        .build());

        // MP refuses auto_return with non-https back URLs (e.g. http://localhost).
        if (frontendUrl.startsWith("https://")) {
            request.autoReturn(STATUS_APPROVED);
        }
        // MP can't reach localhost, so in local dev leave this empty and use the /sync endpoint instead.
        if (!notificationUrl.isBlank()) {
            request.notificationUrl(notificationUrl);
        }

        try {
            Preference preference = new PreferenceClient().create(request.build(), options());
            return new Checkout(preference.getId(), preference.getInitPoint());
        } catch (MPApiException e) {
            log.error("Mercado Pago rejected the preference: {}", e.getApiResponse().getContent());
            throw new PaymentGatewayException("Mercado Pago rejected the payment request", e);
        } catch (MPException e) {
            throw new PaymentGatewayException("Could not reach Mercado Pago", e);
        }
    }

    public Payment getPayment(long paymentId) {
        try {
            return new PaymentClient().get(paymentId, options());
        } catch (MPApiException e) {
            throw new PaymentGatewayException("Mercado Pago could not return payment " + paymentId, e);
        } catch (MPException e) {
            throw new PaymentGatewayException("Could not reach Mercado Pago", e);
        }
    }

    /**
     * The most relevant payment made for a purchase: an approved one if there is any, otherwise the latest attempt.
     */
    public Optional<Payment> findPaymentForPurchase(String externalReference) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("external_reference", externalReference);
        MPSearchRequest search = MPSearchRequest.builder().filters(filters).limit(50).offset(0).build();

        try {
            List<Payment> payments = new PaymentClient().search(search, options()).getResults();
            if (payments == null || payments.isEmpty()) {
                return Optional.empty();
            }
            return payments.stream()
                    .filter(p -> STATUS_APPROVED.equals(p.getStatus()))
                    .findFirst()
                    .or(() -> payments.stream()
                            .filter(p -> p.getDateCreated() != null)
                            .max(Comparator.comparing(Payment::getDateCreated)));
        } catch (MPApiException e) {
            throw new PaymentGatewayException("Mercado Pago could not search payments", e);
        } catch (MPException e) {
            throw new PaymentGatewayException("Could not reach Mercado Pago", e);
        }
    }

    /**
     * Checks the x-signature header MP sends with each webhook.
     * See https://www.mercadopago.com.ar/developers/es/docs/your-integrations/notifications/webhooks
     * If no secret is configured the check is skipped (fine for local dev; set it in production).
     */
    public boolean isValidWebhookSignature(String xSignature, String xRequestId, String dataId) {
        if (webhookSecret.isBlank()) {
            return true;
        }
        if (xSignature == null || xSignature.isBlank()) {
            return false;
        }

        String ts = null;
        String v1 = null;
        for (String part : xSignature.split(",")) {
            String[] kv = part.trim().split("=", 2);
            if (kv.length != 2) continue;
            if (kv[0].equals("ts")) ts = kv[1];
            if (kv[0].equals("v1")) v1 = kv[1];
        }
        if (ts == null || v1 == null) {
            return false;
        }

        StringBuilder manifest = new StringBuilder();
        if (dataId != null && !dataId.isBlank()) manifest.append("id:").append(dataId.toLowerCase()).append(';');
        if (xRequestId != null && !xRequestId.isBlank()) manifest.append("request-id:").append(xRequestId).append(';');
        manifest.append("ts:").append(ts).append(';');

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String expected = HexFormat.of().formatHex(mac.doFinal(manifest.toString().getBytes(StandardCharsets.UTF_8)));
            return MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    v1.toLowerCase().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Could not verify Mercado Pago webhook signature", e);
            return false;
        }
    }

    /** True when the payment covers the purchase total (guards against tampered or partial amounts). */
    public static boolean coversAmount(Payment payment, BigDecimal expected) {
        BigDecimal paid = payment.getTransactionAmount();
        return paid != null && paid.setScale(2, RoundingMode.HALF_UP)
                .compareTo(expected.setScale(2, RoundingMode.HALF_UP)) >= 0;
    }

    private MPRequestOptions options() {
        if (accessToken.isBlank()) {
            throw new PaymentGatewayException("Mercado Pago is not configured (MP_ACCESS_TOKEN is missing)");
        }
        return MPRequestOptions.builder()
                .accessToken(accessToken)
                .connectionTimeout(10_000)
                .connectionRequestTimeout(10_000)
                .socketTimeout(20_000)
                .build();
    }
}
