package com.CoffeeGroup.Coffee.System.features.purchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.CoffeeGroup.Coffee.System.common.exceptions.ProductNotAvailableException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartEntity;
import com.CoffeeGroup.Coffee.System.features.cart.CartItemEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.purchase_detail.PurchaseDetailEntity;

public class PurchaseDraft {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final CafeEntity cafe;
    private final List<PurchaseDetailEntity> details = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;

    private PurchaseDraft(CafeEntity cafe) {
        this.cafe = cafe;
    }

    public static PurchaseDraft from(CartEntity cart) {
        cart.ensureNotEmpty();
        PurchaseDraft draft = new PurchaseDraft(cart.getCafe());
        for (CartItemEntity item : cart.getItems()) {
            draft.addSold(item.getProduct(), item.getQuantity());
        }
        return draft;
    }

    public CafeEntity cafe() {
        return cafe;
    }

    public List<PurchaseDetailEntity> details() {
        return Collections.unmodifiableList(details);
    }

    public BigDecimal total() {
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public int unitsOf(Predicate<CafeProductEntity> product) {
        return details.stream()
                .filter(detail -> product.test(detail.getProduct()))
                .mapToInt(PurchaseDetailEntity::getAmount)
                .sum();
    }

    public void applyPercentageDiscount(BigDecimal percentage) {
        BigDecimal remaining = ONE_HUNDRED.subtract(percentage).divide(ONE_HUNDRED);
        total = total.multiply(remaining);
    }

    public void subtract(BigDecimal amount) {
        total = total.subtract(amount).max(BigDecimal.ZERO);
    }

    public void addGift(CafeProductEntity product) {
        if (product.isActive() && product.belongsTo(cafe) && !product.isAvailable()) {
            throw new ProductNotAvailableException("The coupon product " + product.name() + " is sold out for today");
        }
        product.ensureCanBeSoldBy(cafe);
        details.add(PurchaseDetailEntity.gift(product));
    }

    private void addSold(CafeProductEntity product, int quantity) {
        product.ensureCanBeSoldBy(cafe);
        details.add(PurchaseDetailEntity.sold(product, quantity));
        total = total.add(product.subtotalFor(quantity));
    }
}
