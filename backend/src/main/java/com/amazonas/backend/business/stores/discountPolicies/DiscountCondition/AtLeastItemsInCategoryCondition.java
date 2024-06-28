package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;

import java.util.List;

public class AtLeastItemsInCategoryCondition implements Condition{
    private final int limit;
    private final String categoryName;

    /***
     * Consider to <code>categoryName</code>'s items >= <code>limit</code>
     * @param limit The lower limit of items in the category <code>categoryName</code> needed to realize the discount
     * @param categoryName The category that counts towards the limit
     */
    public AtLeastItemsInCategoryCondition(int limit, String categoryName) {
        this.limit = limit;
        this.categoryName = categoryName;
    }

    @Override
    public boolean decideCondition(List<ProductWithQuantitiy> products) {
        if (products == null ) {
            throw new IllegalArgumentException("products list cannot be null");
        }
        int count = 0;
        for (ProductWithQuantitiy productWithQuantitiy : products) {
            if (productWithQuantitiy.product().category().equals(categoryName)) {
                count += productWithQuantitiy.quantity();
            }
        }
        return count >= limit;
    }
}
