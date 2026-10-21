package com.CoffeeGroup.Coffee.System.features.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;

import com.CoffeeGroup.Coffee.System.features.payment.mercadopago.MercadoPagoService;

class MercadoPagoSignatureTest {

    private static final String SECRET = "test-secret";

    private final MercadoPagoService service = new MercadoPagoService("token", SECRET, "", "http://localhost:5173");

    private static String sign(String manifest) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void acceptsAValidSignature() throws Exception {
        String v1 = sign("id:123;request-id:req-1;ts:1700000000;");

        assertThat(service.isValidWebhookSignature("ts=1700000000,v1=" + v1, "req-1", "123")).isTrue();
    }

    @Test
    void rejectsASignatureForAnotherPayment() throws Exception {
        String v1 = sign("id:123;request-id:req-1;ts:1700000000;");

        assertThat(service.isValidWebhookSignature("ts=1700000000,v1=" + v1, "req-1", "999")).isFalse();
    }

    @Test
    void rejectsMissingOrMalformedHeader() {
        assertThat(service.isValidWebhookSignature(null, "req-1", "123")).isFalse();
        assertThat(service.isValidWebhookSignature("garbage", "req-1", "123")).isFalse();
    }

    @Test
    void skipsTheCheckWhenNoSecretIsConfigured() {
        MercadoPagoService noSecret = new MercadoPagoService("token", "", "", "http://localhost:5173");

        assertThat(noSecret.isValidWebhookSignature(null, null, "123")).isTrue();
    }
}
