package com.amazonas.backend.business.stores.discountPolicies.DiscountComponent;

import com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel.DiscountHierarchyLevel;
import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.common.dtos.Product;

import java.util.List;

public class SimpleDiscount implements DiscountComponent{
    private final int percent;
    private final DiscountHierarchyLevel discountHierarchyLevel;
    public SimpleDiscount(int percent, DiscountHierarchyLevel discountHierarchyLevel) {
        if(percent < 0 || percent > 100)
            throw new IllegalArgumentException("percent must be between 0 and 100");
        this.percent = percent;
        this.discountHierarchyLevel = discountHierarchyLevel;
    }

    int getPercent() {
        return percent;
    }

    /***
     * Designed to prevent a tree with circles
     * @return true if the component is a leaf or all its descendants are leaves
     */
    @Override
    public boolean hasLeavesNode() {
        return true;
    }

    @Override
    public ProductAfterDiscount[] calculateDiscount(List<ProductWithQuantitiy> products) {
        if (products == null) {
            throw new IllegalArgumentException("Products cannot be null");
        }
        int index = 0;
        ProductAfterDiscount[] ret = new ProductAfterDiscount[products.size()];
        for (ProductWithQuantitiy productWithQuantitiy : products) {
            if (productWithQuantitiy == null) {
                throw new IllegalArgumentException("Product cannot be null");
            }
            if (discountHierarchyLevel.isTheProductEligible(productWithQuantitiy.product())) {
                ret[index] = new ProductAfterDiscount(
                                            productWithQuantitiy.product().productId(),
                                            productWithQuantitiy.quantity(),
                                            productWithQuantitiy.product().price(),
                                            productWithQuantitiy.product().price() * (1 - percent)
                                            );
            }
            else {
                ret[index] = new ProductAfterDiscount(
                                            productWithQuantitiy.product().productId(),
                                            productWithQuantitiy.quantity(),
                                            productWithQuantitiy.product().price(),
                                            productWithQuantitiy.product().price()
                                            );
            }
            index++;
        }
        return ret;
    }
}
