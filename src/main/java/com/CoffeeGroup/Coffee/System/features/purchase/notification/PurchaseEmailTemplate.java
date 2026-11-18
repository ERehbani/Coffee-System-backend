package com.CoffeeGroup.Coffee.System.features.purchase.notification;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

@Component
public class PurchaseEmailTemplate {

    private static final String LINE =
            "<tr><td>%s</td><td style='text-align:center;'>%d</td>"
            + "<td style='text-align:center;'>$%s</td><td style='text-align:center;'>$%s</td></tr>";

    private final String confirmedTemplate = load("templates/email/purchase-confirmed.html");
    private final String cancelledTemplate = load("templates/email/purchase-cancelled.html");

    public String confirmed(PurchaseReceipt receipt) {
        return fill(confirmedTemplate, receipt, Map.of());
    }

    public String cancelled(PurchaseReceipt receipt, int revokedPoints) {
        return fill(cancelledTemplate, receipt, Map.of("revokedPoints", String.valueOf(revokedPoints)));
    }

    private static String fill(String template, PurchaseReceipt receipt, Map<String, String> extra) {
        String html = template
                .replace("{{clientName}}", escape(receipt.clientName()))
                .replace("{{purchaseId}}", escape(receipt.purchaseId().toString()))
                .replace("{{cafeName}}", escape(receipt.cafeName()))
                .replace("{{total}}", money(receipt.total()))
                .replace("{{lines}}", lines(receipt));
        for (Map.Entry<String, String> value : extra.entrySet()) {
            html = html.replace("{{" + value.getKey() + "}}", escape(value.getValue()));
        }
        return html;
    }

    private static String lines(PurchaseReceipt receipt) {
        return receipt.lines().stream()
                .map(line -> String.format(LINE, escape(line.productName()), line.amount(),
                        money(line.unitPrice()), money(line.subtotal())))
                .collect(Collectors.joining());
    }

    private static String money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private static String escape(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value);
    }

    private static String load(String path) {
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Falta la plantilla de email " + path, e);
        }
    }
}
