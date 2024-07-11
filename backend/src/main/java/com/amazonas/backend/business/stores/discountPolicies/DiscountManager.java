package com.amazonas.backend.business.stores.discountPolicies;

import com.amazonas.backend.business.stores.discountPolicies.DiscountComponent.*;
import com.amazonas.backend.business.stores.discountPolicies.DiscountCondition.*;
import com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel.CategoryLevel;
import com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel.ProductLevel;
import com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel.StoreLevel;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.DiscountDTOs.*;

import java.util.LinkedList;
import java.util.List;

public class DiscountManager {
    private DiscountComponent discountComponent;
    public DiscountManager() {
        discountComponent = null;
    }

    public DiscountComponentDTO getDiscountPolicyDTO() throws StoreException {
        if (discountComponent == null) {
            return null;
        }
        return discountComponent.generateDTO();
    }

    public String getDiscountPolicyCFG() throws StoreException {
        if (discountComponent == null) {
            return null;
        }
        return discountComponent.generateCFG();
    }

    public boolean deleteAllDiscounts() {
        discountComponent = null;
        return true;
    }

    /**
     *
     * @param products list of the products and their quantity from the cart
     * @return an array with the product and his final price
     * @throws StoreException
     */
    public ProductAfterDiscount[] applyDiscountPolicy(List<ProductWithQuantitiy> products) throws StoreException {
        if (discountComponent == null) {
            ProductAfterDiscount[] productsAfterDiscounts = new ProductAfterDiscount[products.size()];
            int index = 0;
            for (ProductWithQuantitiy product : products) {
                productsAfterDiscounts[index++] = new ProductAfterDiscount(product.product().getProductId(),
                                                                           product.quantity(),
                                                                           product.product().getPrice(),
                                                                           product.product().getPrice());
            }
            return productsAfterDiscounts;
        }
        return discountComponent.calculateDiscount(products);
    }

    /**
     *
     * @param discountComponentDTO
     * @return a cfg of the new discount policy
     * @throws StoreException
     */
    public String changeDiscountPolicy(DiscountComponentDTO discountComponentDTO) throws StoreException {
        discountComponent = translateDiscountComponentDTO(discountComponentDTO);
        return discountComponent.generateCFG();
    }

    private DiscountComponent translateDiscountComponentDTO(DiscountComponentDTO discountComponentDTO) throws StoreException {
        switch (discountComponentDTO) {
            case null -> throw new StoreException("Discount policy cannot be null");

            // simple discount
            case SimpleDiscountDTO simpleDiscountDTO -> {
                if (simpleDiscountDTO.percent() > 100 || simpleDiscountDTO.percent() < 1) {
                    throw new StoreException("Discount policy percent must be between 0 and 100");
                }
                if (simpleDiscountDTO.hierarchyLevel() == null) {
                    throw new StoreException("Discount policy hierarchyLevel cannot be null");
                }
                else if (simpleDiscountDTO.hierarchyLevel() == HierarchyLevel.StoreLevel) {
                    return new SimpleDiscount(simpleDiscountDTO.percent(), new StoreLevel());
                }
                else if (simpleDiscountDTO.hierarchyLevel() == HierarchyLevel.CategoryLevel) {
                    if (simpleDiscountDTO.text() == null) {
                        throw new StoreException("Discount policy text cannot be null");
                    }
                    return new SimpleDiscount(simpleDiscountDTO.percent(), new CategoryLevel(simpleDiscountDTO.text()));
                }
                else if (simpleDiscountDTO.hierarchyLevel() == HierarchyLevel.ProductLevel) {
                    if (simpleDiscountDTO.text() == null) {
                        throw new StoreException("Discount policy text cannot be null");
                    }
                    return new SimpleDiscount(simpleDiscountDTO.percent(), new ProductLevel(simpleDiscountDTO.text()));
                }
                else {
                    throw new StoreException("Unknown discount policy");
                }
            }

            // multiple discount
            case MultipleDiscountDTO multipleDiscountDTO -> {
                if (multipleDiscountDTO.discountComponents() == null || multipleDiscountDTO.discountComponents().isEmpty()) {
                    throw new StoreException("Discount policy cannot be empty");
                }
                else if (multipleDiscountDTO.multipleDiscountType() == null) {
                    throw new StoreException("Discount policy type cannot be empty");
                }
                List<DiscountComponent> discountComponents = new LinkedList<>();
                for (DiscountComponentDTO dto : multipleDiscountDTO.discountComponents()) {
                    discountComponents.add(translateDiscountComponentDTO(dto));
                }
                if (multipleDiscountDTO.multipleDiscountType() == MultipleDiscountType.ADDITION) {
                    return new AdditionDiscount(discountComponents);
                }
                else if (multipleDiscountDTO.multipleDiscountType() == MultipleDiscountType.MAXIMUM_PRICE) {
                    return new MaxDiscount(discountComponents);
                } else if (multipleDiscountDTO.multipleDiscountType() == MultipleDiscountType.MINIMUM_PRICE) {
                    return new XorDiscount(discountComponents, XorDecisionRule.THE_LOWEST_ONE);
                }
                else {
                    throw new StoreException("Unknown discount policy");
                }
            }

            // complex discount
            case ComplexDiscountDTO complexDiscountDTO -> {
                if (complexDiscountDTO.discountCondition() == null) {
                    throw new StoreException("Condition of discount cannot be empty");
                }
                else if (complexDiscountDTO.discountComponentDTO() == null) {
                    throw new StoreException("Discount cannot be empty");
                }
                return new ComplexDiscount(translateDiscountConditionDTO(complexDiscountDTO.discountCondition()),
                                           translateDiscountComponentDTO(complexDiscountDTO.discountComponentDTO()));
            }
            default -> throw new StoreException("Invalid discount policy");
        }
    }

    private Condition translateDiscountConditionDTO(DiscountConditionDTO discountConditionDTO) throws StoreException {
        switch (discountConditionDTO) {
            case null -> throw new StoreException("Discount condition cannot be null");

            case UnaryConditionDTO unaryConditionDTO -> {
                if (unaryConditionDTO.number() <= 0) {
                    throw new StoreException("Discount condition limit must be greater than zero");
                }
                else if (unaryConditionDTO.unaryConditionType() == UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY) {
                    if (unaryConditionDTO.text() == null || unaryConditionDTO.text().isEmpty()) {
                        throw new StoreException("Discount condition's category name cannot be empty");
                    }
                    return new AtLeastItemsInCategoryCondition(unaryConditionDTO.number(), unaryConditionDTO.text());
                }
                else if (unaryConditionDTO.unaryConditionType() == UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT) {
                    if (unaryConditionDTO.text() == null || unaryConditionDTO.text().isEmpty()) {
                        throw new StoreException("Discount condition's PRODUCT ID cannot be empty");
                    }
                    return new AtLeastSomeProductsCondition(unaryConditionDTO.number(), unaryConditionDTO.text());
                } else if (unaryConditionDTO.unaryConditionType() == UnaryConditionType.AT_LEAST_SOME_PRICE) {
                    return new AtLeastPriceCondition(unaryConditionDTO.number());
                }
                else {
                    throw new StoreException("Unknown discount condition");
                }
            }

            case MultipleConditionDTO multipleConditionDTO -> {
                if (multipleConditionDTO.conditions() == null || multipleConditionDTO.conditions().isEmpty()) {
                    throw new StoreException("Condition of discount cannot be empty");
                }
                Condition[] conditions = new Condition[multipleConditionDTO.conditions().size()];
                int index = 0;
                for (DiscountConditionDTO conditionDTO : multipleConditionDTO.conditions()) {
                    conditions[index++] = translateDiscountConditionDTO(conditionDTO);
                }
                if (multipleConditionDTO.multipleConditionType() == MultipleConditionType.AND) {
                    return new AndCondition(conditions);
                }
                else if (multipleConditionDTO.multipleConditionType() == MultipleConditionType.OR) {
                    return new OrCondition(conditions);
                } else if (multipleConditionDTO.multipleConditionType() == MultipleConditionType.XOR) {
                    return new XorCondition(conditions);
                }
                else {
                    throw new StoreException("Unknown discount condition");
                }
            }

            default -> throw new StoreException("Invalid discount condition");
        }
    }

}
