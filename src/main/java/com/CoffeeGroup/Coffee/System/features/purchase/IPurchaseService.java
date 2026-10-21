package com.CoffeeGroup.Coffee.System.features.purchase;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseStatusRequest;


public interface IPurchaseService {
    PurchaseResponse create(CreatePurchaseRequest request);

    List<PurchaseResponse> findAll(PurchaseStatus status, Date from, Date to);

    PurchaseResponse getById(UUID externalId);

    PurchaseResponse updateStatus(UUID externalId, UpdatePurchaseStatusRequest request);

    List<PurchaseResponse> getHistorialByCafe(UUID cafeId, PurchaseStatus status, Date from, Date to);

    List<PurchaseResponse> getHistorialByCliente(UUID clientId);

    /** Called from the Mercado Pago webhook. */
    void handleMercadoPagoNotification(long paymentId);

    /** Asks Mercado Pago for the purchase's payment and updates it. Used by the front after paying. */
    PurchaseResponse syncMercadoPagoPayment(UUID purchaseId);

    /** The client gives up on a pending purchase; any benefit code used is given back. */
    PurchaseResponse cancelPending(UUID purchaseId);
}