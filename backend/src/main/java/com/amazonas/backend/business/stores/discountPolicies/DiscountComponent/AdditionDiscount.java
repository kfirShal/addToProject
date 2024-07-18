package com.amazonas.backend.business.stores.discountPolicies.DiscountComponent;

import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.DiscountDTOs.MultipleDiscountDTO;
import com.amazonas.common.DiscountDTOs.MultipleDiscountType;

import java.util.ArrayList;
import java.util.List;

public class AdditionDiscount implements DiscountComponent{
    final private boolean isLeavesNode;
    final DiscountComponent[] children;

    public AdditionDiscount(List<DiscountComponent> children) {
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
        ProductAfterDiscount[][] childrenResult = new ProductAfterDiscount[children.length][];
        ProductAfterDiscount[] ret = new ProductAfterDiscount[products.size()];
        int index = 0;
        for (DiscountComponent child : children) { //calculate all the discounts
            childrenResult[index++] = child.calculateDiscount(products);
        }
        for (int productIndex = 0; productIndex < products.size(); productIndex++) {
            double initialPrice = childrenResult[0][productIndex].originalPrice();
            for (ProductAfterDiscount[] productsAfterDiscounts : childrenResult) {
                initialPrice -= (productsAfterDiscounts[productIndex].originalPrice() -
                        productsAfterDiscounts[productIndex].priceAfterDiscount());
                if (initialPrice < 0) {
                    initialPrice = 0;// combine the discounts
                }
            }
            ret[productIndex] = new ProductAfterDiscount(
                                        childrenResult[0][productIndex].productId(),
                                        childrenResult[0][productIndex].quantity(),
                                        childrenResult[0][productIndex].originalPrice(),
                                        initialPrice
                                    );
        }
        return ret;
    }

    @Override
    public DiscountComponentDTO generateDTO() throws StoreException {
        List<DiscountComponentDTO> discounts = new ArrayList<>();
        for (DiscountComponent child : children) {
            discounts.add(child.generateDTO());
        }
        return new MultipleDiscountDTO( MultipleDiscountType.ADDITION, discounts);
    }

    @Override
    public String generateCFG() throws StoreException {
        StringBuilder ret = new StringBuilder("( add");
        for (DiscountComponent child : children) {
            ret.append(" ").append(child.generateCFG());
        }
        return ret + ") ";
    }


}
