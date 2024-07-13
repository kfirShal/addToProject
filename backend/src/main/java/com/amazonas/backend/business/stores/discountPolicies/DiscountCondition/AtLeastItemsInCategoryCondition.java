package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.common.DiscountDTOs.DiscountConditionDTO;
import com.amazonas.common.DiscountDTOs.UnaryConditionDTO;
import com.amazonas.common.DiscountDTOs.UnaryConditionType;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;

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
            if (productWithQuantitiy.product().getCategory().equals(categoryName)) {
                count += productWithQuantitiy.quantity();
            }
        }
        return count >= limit;
    }

    @Override
    public DiscountConditionDTO generateDTO() throws StoreException {
        return new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, limit, categoryName);
    }

    @Override
    public String generateCFG() throws StoreException {
        return "( category-quantity-more-than " + categoryName + " " + limit + " )";
    }


}
