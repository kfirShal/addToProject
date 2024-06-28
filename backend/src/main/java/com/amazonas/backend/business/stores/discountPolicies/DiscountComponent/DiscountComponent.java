package com.amazonas.backend.business.stores.discountPolicies.DiscountComponent;

import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.common.dtos.Product;

import java.util.List;

public interface DiscountComponent {
    /***
     * Designed to prevent a tree with circles
     * @return true if the component is a leaf or all its descendants are leaves
     */
    boolean hasLeavesNode();
    ProductAfterDiscount[] calculateDiscount(List<ProductWithQuantitiy> products);
}
