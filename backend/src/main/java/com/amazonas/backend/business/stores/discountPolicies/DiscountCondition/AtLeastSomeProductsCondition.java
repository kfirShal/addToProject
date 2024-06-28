package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;

import java.util.List;

public class AtLeastSomeProductsCondition implements Condition{
    private final int limit;
    private final String productId;

    /***
     * Consider to items with <code>productId</code> >= <code>limit</code>
     * @param limit The lower limit of items with <code>productId</code> needed to realize the discount
     * @param productId The category that counts towards the limit
     */
    public AtLeastSomeProductsCondition(int limit, String productId) {
        this.limit = limit;
        this.productId = productId;
    }

    @Override
    public boolean decideCondition(List<ProductWithQuantitiy> products) {
        if (products == null ) {
            throw new IllegalArgumentException("products list cannot be null");
        }
        int count = 0;
        for (ProductWithQuantitiy productWithQuantitiy : products) {
            if (productWithQuantitiy.product().productId().equals(productId)) {
                count += productWithQuantitiy.quantity();
            }
        }
        return count >= limit;
    }
}
