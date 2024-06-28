package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;

import java.util.List;

public class AtLeastPriceCondition implements Condition{
    private final double limit;

    /***
     * Consider to cart's total price >= <code>limit</code>
     * @param limit The lower limit of price to realize the discount
     */
    public AtLeastPriceCondition(double limit) {
        this.limit = limit;
    }

    @Override
    public boolean decideCondition(List<ProductWithQuantitiy> products) {
        if (products == null ) {
            throw new IllegalArgumentException("products list cannot be null");
        }
        double count = 0;
        for (ProductWithQuantitiy productWithQuantitiy : products) {
            count += productWithQuantitiy.product().price() * productWithQuantitiy.quantity();
        }
        return count >= limit;
    }
}
