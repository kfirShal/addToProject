package com.amazonas.backend.business.stores.discountPolicies.DiscountComponent;

import com.amazonas.backend.business.stores.discountPolicies.DiscountCondition.Condition;
import com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs.ComplexDiscountDTO;
import com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel.DiscountHierarchyLevel;
import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.dtos.Product;

import java.util.List;

public class ComplexDiscount implements DiscountComponent {
    final private boolean isLeavesNode;
    private final Condition condition;
    private final DiscountComponent discount;
    public ComplexDiscount(Condition condition, DiscountComponent discount) {
        if (condition == null) {
            throw new NullPointerException("the condition cannot be null");
        }
        if (discount == null) {
            throw new NullPointerException("the discount cannot be null");
        }
        this.condition = condition;
        this.discount = discount;
        this.isLeavesNode = discount.hasLeavesNode();
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
        if (products == null) {
            throw new IllegalArgumentException("Products cannot be null");
        }
        if (condition.decideCondition(products)) {
            return discount.calculateDiscount(products);
        }
        else {
            ProductAfterDiscount[] ret = new ProductAfterDiscount[products.size()];
            int index = 0;
            for (ProductWithQuantitiy product : products) {
                if (product == null) {
                    throw new IllegalArgumentException("Product cannot be null");
                }
                ret[index++] = new ProductAfterDiscount(
                                                        product.product().productId(),
                                                        product.quantity(),
                                                        product.product().price(),
                                                        product.product().price()
                                                        );
            }
            return ret;
        }
    }

    @Override
    public DiscountComponentDTO generateDTO() throws StoreException {
        if(condition == null || discount == null) {
            throw new StoreException("cannot generate discount component");
        }
        return new ComplexDiscountDTO(condition.generateDTO(), discount.generateDTO());
    }
}
