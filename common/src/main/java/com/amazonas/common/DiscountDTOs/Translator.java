package com.amazonas.common.DiscountDTOs;


import com.amazonas.common.DiscountDTOs.*;
import com.amazonas.common.exceptions.DiscountPolicyException;

import java.util.ArrayList;
import java.util.List;

public class Translator {
    public static DiscountComponentDTO translator(String cfgText) throws DiscountPolicyException {
        Node discountTree = Parser.parseCode(cfgText);
        return translateNode(discountTree);
    }

    private static DiscountComponentDTO translateNode(Node node) throws DiscountPolicyException {
        if (node == null) {
            throw new DiscountPolicyException("Unexpected error11", 0, 0);
        }
        else if (node.isString) {
            throw new DiscountPolicyException("Unexpected error12", 0, 0);
        }
        else {
            if (node.children == null || node.children.length == 0) {
                throw new DiscountPolicyException("Unexpected error13", 0, 0);
            }
            if (node.children[0].isString) {
                switch (node.children[0].content) {
                    case "minimal-price", "maximal-discount", "add" -> {
                        List<DiscountComponentDTO> discounts = new ArrayList<>();
                        for (int i = 1; i < node.children.length; i++) {
                            discounts.add(translateNode(node.children[i]));
                        }
                        return switch (node.children[0].content) {
                            case "minimal-price" ->
                                    new MultipleDiscountDTO(MultipleDiscountType.MINIMUM_PRICE, discounts);
                            case "maximal-discount" ->
                                    new MultipleDiscountDTO(MultipleDiscountType.MAXIMUM_PRICE, discounts);
                            case "add" -> new MultipleDiscountDTO(MultipleDiscountType.ADDITION, discounts);
                            default -> throw new DiscountPolicyException("Unexpected error14", 0, 0);
                        };
                    }
                    case "global-discount" -> {
                        return new SimpleDiscountDTO(HierarchyLevel.StoreLevel, "", Integer.parseInt(node.children[1].content));
                    }
                    case "product-discount" -> {
                        return new SimpleDiscountDTO(HierarchyLevel.ProductLevel, node.children[1].content, Integer.parseInt(node.children[2].content));
                    }
                    case "category-discount" -> {
                        return new SimpleDiscountDTO(HierarchyLevel.CategoryLevel, node.children[1].content, Integer.parseInt(node.children[2].content));
                    }
                    case "if-then" -> {
                        return new ComplexDiscountDTO(nodeToCondition(node.children[1]), translateNode(node.children[2]));
                    }
                    default -> throw new DiscountPolicyException("Unexpected error15", 0, 0);
                }
            }
            else {
                throw  new DiscountPolicyException("Unexpected error16", 0, 0);
            }
        }
    }

    private static DiscountConditionDTO nodeToCondition(Node node) throws DiscountPolicyException {
        if (node == null) {
            throw new DiscountPolicyException("Unexpected error17", 0, 0);
        }
        else if (node.isString) {
            throw new DiscountPolicyException("Unexpected error18", 0, 0);
        }
        else {
            if (node.children == null || node.children.length == 0) {
                throw new DiscountPolicyException("Unexpected error19", 0, 0);
            }
            if (node.children[0].isString) {
                switch (node.children[0].content) {
                    case "price-over" -> {
                        return new UnaryConditionDTO(UnaryConditionType.AT_LEAST_SOME_PRICE, Integer.parseInt(node.children[1].content), "");
                    }
                    case "product-quantity-more-than" -> {
                        return new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_SOME_PRODUCT, Integer.parseInt(node.children[2].content), node.children[1].content);
                    }
                    case "category-quantity-more-than" -> {
                        return new UnaryConditionDTO(UnaryConditionType.AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY, Integer.parseInt(node.children[2].content), node.children[1].content);
                    }
                    case "&" , "|", "^" -> {
                        List<DiscountConditionDTO> conds = new ArrayList<>();
                        for (int i = 1; i < node.children.length; i++) {
                            conds.add(nodeToCondition(node.children[i]));
                        }
                        return switch (node.children[0].content) {
                            case "&" -> new MultipleConditionDTO(MultipleConditionType.AND, conds);
                            case "|" -> new MultipleConditionDTO(MultipleConditionType.OR, conds);
                            case "^" -> new MultipleConditionDTO(MultipleConditionType.XOR, conds);
                            default -> throw new DiscountPolicyException("Unexpected error20", 0, 0);
                        };
                    }
                    default -> throw new DiscountPolicyException("Unexpected error21", 0, 0);
                }
            }
            else {
                throw  new DiscountPolicyException("Unexpected error22", 0, 0);
            }
        }
    }

    //for debugging
    private static void printNode(Node node, int tabs) throws DiscountPolicyException {
        for (int i = 0; i < tabs; i++) {
            System.out.print("\t");
        }
        if (node == null) {
            System.out.println("null");
        }
        else if (node.isString) {
            System.out.println (node.content);
        }
        else {
            for (int i = 0; i < node.children.length; i++) {
                printNode(node.children[i], tabs + 1);
            }
        }
    }
}
