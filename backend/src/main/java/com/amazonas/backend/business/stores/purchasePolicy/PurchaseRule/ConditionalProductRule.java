package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;

import java.util.List;

public class ConditionalProductRule implements PurchaseRule {
    private final String productID;
    private final int quantity;
    private final PurchaseRule purchaseRule;

    public ConditionalProductRule(String productID, int quantity, PurchaseRule purchaseRule) throws StoreException {
        if (productID == null || productID.isEmpty()) {
            throw new StoreException("product ID cannot be empty");
        }
        if (purchaseRule == null) {
            throw new StoreException("purchase policy cannot be empty");
        }
        if (quantity < 0) {
            throw new StoreException("quantity cannot be negative");
        }
        this.productID = productID;
        this.quantity = quantity;
        this.purchaseRule = purchaseRule;
    }

    public String getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public PurchaseRule getPurchaseRule() {
        return purchaseRule;
    }

    @Override
    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        int productCount = 0;
        for (ProductWithQuantitiy product : products) {
            if (product.product().getProductId().equals(productID)) {
                productCount += product.quantity();
            }
        }
        if (productCount >= quantity) {
            return purchaseRule.isSatisfied(products, user);
        }
        return true;
    }
}
