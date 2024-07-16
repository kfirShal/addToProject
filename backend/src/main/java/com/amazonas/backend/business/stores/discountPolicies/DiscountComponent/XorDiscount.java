package com.amazonas.backend.business.stores.discountPolicies.DiscountComponent;

import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.DiscountDTOs.MultipleDiscountDTO;
import com.amazonas.common.DiscountDTOs.MultipleDiscountType;
import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;

import java.util.ArrayList;
import java.util.List;

public class XorDiscount implements DiscountComponent {
    final private boolean isLeavesNode;
    final XorDecisionRule xorDecisionRule;
    final DiscountComponent[] children;

    public XorDiscount (List<DiscountComponent> children, XorDecisionRule xorDecisionRule) {
        this.xorDecisionRule = xorDecisionRule;
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
        double value;
        if (xorDecisionRule == XorDecisionRule.THE_LOWEST_ONE) {
            value = Double.MAX_VALUE;
        }
        else if (xorDecisionRule == XorDecisionRule.THE_HIGHEST_ONE) {
            value = Double.MIN_VALUE;
        }
        else {
            throw new IllegalArgumentException("The xor condition must be one of the list");
        }
        for (DiscountComponent child : children) { //calculate all the discounts
            ProductAfterDiscount[] current = child.calculateDiscount(products);
            double totalPrice = 0;
            for (ProductAfterDiscount productAfterDiscount : current) {
                totalPrice += productAfterDiscount.priceAfterDiscount() * productAfterDiscount.quantity();
            }
            if (xorDecisionRule == XorDecisionRule.THE_LOWEST_ONE) {
                if (totalPrice < value) {
                    value = totalPrice;
                    ret = current;
                }
            }
            else if (xorDecisionRule == XorDecisionRule.THE_HIGHEST_ONE) {
                if (totalPrice > value) {
                    value = totalPrice;
                    ret = current;
                }
            }
        }
        return ret;
    }

    @Override
    public DiscountComponentDTO generateDTO() throws StoreException {
        List<DiscountComponentDTO> discounts = new ArrayList<>();
        for (DiscountComponent child : children) {
            discounts.add(child.generateDTO());
        }
        if (xorDecisionRule == XorDecisionRule.THE_LOWEST_ONE) {
            return new MultipleDiscountDTO(discounts, MultipleDiscountType.MINIMUM_PRICE);
        } else if (xorDecisionRule == XorDecisionRule.THE_HIGHEST_ONE) {
            return new MultipleDiscountDTO(discounts, MultipleDiscountType.MAXIMUM_PRICE);
        }
        else {
            throw new IllegalArgumentException("The xor condition must be one of the list");
        }
    }

    @Override
    public String generateCFG() throws StoreException {
        StringBuilder ret = null;
        if (xorDecisionRule == XorDecisionRule.THE_LOWEST_ONE) {
            ret = new StringBuilder("( minimal-price");
        }
        else if (xorDecisionRule == XorDecisionRule.THE_HIGHEST_ONE) {
            ret = new StringBuilder("( maximal-price");
        }
        else {
            throw new IllegalArgumentException("Cannot generate the CFG");
        }
        for (DiscountComponent child : children) {
            ret.append(" ").append(child.generateCFG());
        }
        return ret + ") ";
    }
}
