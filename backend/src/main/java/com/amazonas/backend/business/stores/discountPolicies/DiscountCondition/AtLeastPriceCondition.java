package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.common.DiscountDTOs.DiscountConditionDTO;
import com.amazonas.common.DiscountDTOs.UnaryConditionDTO;
import com.amazonas.common.DiscountDTOs.UnaryConditionType;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;

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

    @Override
    public DiscountConditionDTO generateDTO() throws StoreException {
        return new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, (int)limit, "");
    }

    @Override
    public String generateCFG() throws StoreException {
        return "( price-over " + limit + " )";
    }
}
