package com.amazonas.backend.business.stores.discountPolicies.DiscountComponent;

import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;

import java.util.List;

public class MaxDiscount implements DiscountComponent {
    final private boolean isLeavesNode;
    final DiscountComponent[] children;

    public MaxDiscount (List<DiscountComponent> children) {
        this.children = new DiscountComponent[children.size()];
        int index = 0;
        for (DiscountComponent child : children) {
            if (child == null) {
                throw new IllegalArgumentException("all the discount components must not be empty");
            }
            if (!child.hasLeavesNode()) {
                throw new IllegalArgumentException("The discount cannot have a circle");
            }
            this.children[index++] = (child);
        }
        isLeavesNode = true;
    }

    /***
     * Designed to prevent a tree with circles
     * @return true if the component is a leaf or all its descendants are leaves
     */
    @Override
    public boolean hasLeavesNode() {
        return isLeavesNode;
    }

    @Override
    public ProductAfterDiscount[] calculateDiscount(List<ProductWithQuantitiy> products) {
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("products must not be empty");
        }
        ProductAfterDiscount[] ret = children[0].calculateDiscount(products);
        double value = Double.MIN_VALUE;
        for (DiscountComponent child : children) { //calculate all the discounts
            ProductAfterDiscount[] current = child.calculateDiscount(products);
            double totalPrice = 0;
            for (ProductAfterDiscount productAfterDiscount : current) {
                totalPrice += productAfterDiscount.priceAfterDiscount() * productAfterDiscount.quantity();
                if (totalPrice > value) {
                    value = totalPrice;
                    ret = current;
                }
            }
        }
        return ret;
    }
}
