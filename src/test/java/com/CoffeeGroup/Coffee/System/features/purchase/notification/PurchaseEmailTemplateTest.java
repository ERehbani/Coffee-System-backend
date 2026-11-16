package com.CoffeeGroup.Coffee.System.features.purchase.notification;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PurchaseEmailTemplateTest {

    private final PurchaseEmailTemplate template = new PurchaseEmailTemplate();

    private final PurchaseReceipt receipt = new PurchaseReceipt(
            "ana@test.com",
            "Ana <script>",
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "Cafe Central",
            new BigDecimal("5000"),
            List.of(new PurchaseReceipt.Line("Capuccino & leche", 2, new BigDecimal("2500"), new BigDecimal("5000"))));

    @Test
    void theConfirmationListsTheProductsAndTheTotal() {
        String html = template.confirmed(receipt);

        assertThat(html).contains("Cafe Central", "11111111-1111-1111-1111-111111111111", "$5000.00", "2500.00");
        assertThat(html).doesNotContain("{{");
    }

    @Test
    void valuesAreEscapedSoTheyCannotInjectHtml() {
        String html = template.confirmed(receipt);

        assertThat(html).contains("Ana &lt;script&gt;", "Capuccino &amp; leche");
        assertThat(html).doesNotContain("<script>");
    }

    @Test
    void theCancellationTellsHowManyPointsWereTakenBack() {
        String html = template.cancelled(receipt, 160);

        assertThat(html).contains("fue cancelada", "160 puntos");
        assertThat(html).doesNotContain("{{");
    }
}
