package com.CoffeeGroup.Coffee.System.features.payment;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.payment.mercadopago.MercadoPagoService;
import com.CoffeeGroup.Coffee.System.features.purchase.IPurchaseService;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment methods and Mercado Pago checkout")
public class PaymentController {

    private final IPurchaseService purchaseService;
    private final MercadoPagoService mercadoPagoService;

    @GetMapping("/methods")
    @Operation(summary = "List payment methods", description = "Payment methods the front can offer at checkout")
    public ResponseEntity<List<PaymentMethod>> methods() {
        return ResponseEntity.ok(Arrays.asList(PaymentMethod.values()));
    }

    @PostMapping("/purchases/{purchaseId}/sync")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Refresh payment status",
            description = "Asks Mercado Pago for the purchase's payment and updates the purchase. "
                    + "Call it when the client comes back from Mercado Pago.")
    public ResponseEntity<PurchaseResponse> sync(@PathVariable UUID purchaseId) {
        return ResponseEntity.ok(purchaseService.syncMercadoPagoPayment(purchaseId));
    }

    @PostMapping("/purchases/{purchaseId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE', 'ROLE_SUPERADMIN')")
    @Operation(summary = "Cancel pending purchase", description = "Cancels a purchase that hasn't been paid yet")
    public ResponseEntity<PurchaseResponse> cancel(@PathVariable UUID purchaseId) {
        return ResponseEntity.ok(purchaseService.cancelPending(purchaseId));
    }

    /**
     * Mercado Pago notifications. Public (no JWT). The payment is always fetched again from MP,
     * so the body is never trusted. Answering non-2xx makes MP retry later.
     */
    @PostMapping("/mercadopago/webhook")
    @Operation(summary = "Mercado Pago webhook", description = "Called by Mercado Pago, not by the front")
    public ResponseEntity<Void> mercadoPagoWebhook(
            @RequestParam Map<String, String> params,
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestBody(required = false) Map<String, Object> body) {

        String topic = firstNonBlank(params.get("type"), params.get("topic"), body != null ? str(body.get("type")) : null);
        String dataIdFromQuery = firstNonBlank(params.get("data.id"), params.get("id"));
        String paymentId = firstNonBlank(dataIdFromQuery, dataIdFromBody(body));

        if (!"payment".equals(topic) || paymentId == null) {
            return ResponseEntity.ok().build(); // Other topics (merchant_order, ...) aren't used.
        }

        if (!mercadoPagoService.isValidWebhookSignature(xSignature, xRequestId, dataIdFromQuery)) {
            log.warn("Rejected Mercado Pago webhook with invalid signature (payment {})", paymentId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long id;
        try {
            id = Long.parseLong(paymentId);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok().build();
        }

        purchaseService.handleMercadoPagoNotification(id);
        return ResponseEntity.ok().build();
    }

    private static String dataIdFromBody(Map<String, Object> body) {
        if (body != null && body.get("data") instanceof Map<?, ?> data) {
            return str(data.get("id"));
        }
        return null;
    }

    private static String str(Object value) {
        return value != null ? value.toString() : null;
    }

    private static String firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
