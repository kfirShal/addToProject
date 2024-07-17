package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;

import java.util.List;

public class ConditionalCategoryRule implements PurchaseRule {
    private final String categoryName;
    private final int quantity;
    private final PurchaseRule purchaseRule;

    public ConditionalCategoryRule(String categoryName, int quantity, PurchaseRule purchaseRule) throws StoreException {
        if (categoryName == null || categoryName.isEmpty()) {
            throw new StoreException("category name cannot be empty");
        }
        if (purchaseRule == null) {
            throw new StoreException("purchase policy cannot be empty");
        }
        if (quantity < 0) {
            throw new StoreException("quantity cannot be negative");
        }
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.purchaseRule = purchaseRule;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public PurchaseRule getPurchaseRule() {
        return purchaseRule;
    }

    @Override
    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        int categoryCount = 0;
        for (ProductWithQuantitiy product : products) {
            if (product.product().getCategory().equals(categoryName)) {
                categoryCount += product.quantity();
            }
        }
        if (categoryCount >= quantity) {
            return purchaseRule.isSatisfied(products, user);
        }
        return true;
    }

    @Override
    public String generateCFG() {
        return "( if-check ( category-quantity-more-than " + categoryName + " " + quantity + " ) " + purchaseRule.generateCFG() + " )";
    }
}
