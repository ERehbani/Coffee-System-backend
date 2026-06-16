package com.CoffeeGroup.Coffee.System.common.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;

@Component
public class EmailService {
    @Value("${RESEND_APIKEY}")
    private String resendApiKey;

    private Resend resend;

    @PostConstruct
    public void init() {
        String cleanApiKey = resendApiKey.replaceAll("^\"|\"$", "");
        System.out.println("RESEND_APIKEY value: " + cleanApiKey);
        this.resend = new Resend(cleanApiKey);
    }

    public void sendPurchaseConfirmation(String subject, String clientName, UUID purchaseId, BigDecimal totalPrice, List<PurchaseDetailEntity> details) {
        StringBuilder productsHtml = new StringBuilder();
        productsHtml.append("<table border='1' cellpadding='10' cellspacing='0' style='border-collapse: collapse; width: 100%;'>");
        productsHtml.append("<tr style='background-color: #f2f2f2;'>");
        productsHtml.append("<th style='text-align: left;'>Producto</th>");
        productsHtml.append("<th style='text-align: center;'>Cantidad</th>");
        productsHtml.append("<th style='text-align: center;'>Precio Unitario</th>");
        productsHtml.append("<th style='text-align: center;'>Subtotal</th>");
        productsHtml.append("</tr>");

        for (PurchaseDetailEntity detail : details) {
            String productName = detail.getProduct().getName();
            int amount = detail.getAmount();
            BigDecimal unitPrice = detail.getUnitPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(amount));

            productsHtml.append("<tr>");
            productsHtml.append("<td>").append(productName).append("</td>");
            productsHtml.append("<td style='text-align: center;'>").append(amount).append("</td>");
            productsHtml.append("<td style='text-align: center;'>$").append(unitPrice).append("</td>");
            productsHtml.append("<td style='text-align: center;'>$").append(subtotal).append("</td>");
            productsHtml.append("</tr>");
        }

        productsHtml.append("</table>");

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Acme <onboarding@resend.dev>")
                .to("elianrhbn@gmail.com")
                .subject(subject)
                .html("<h2>Hola " + clientName + "</h2>" +
                        "<p>Tu compra fue confirmada exitosamente.</p>" +
                        "<p><strong>ID de compra:</strong> " + purchaseId.toString() + "</p>" +
                        "<h3>Detalle de productos:</h3>" +
                        productsHtml.toString() +
                        "<p style='margin-top: 20px;'><strong>Total:</strong> $" + totalPrice + "</p>" +
                        "<p>¡Gracias por tu compra!</p>")
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }
}
