package com.amazonas.backend.business.stores.purchasePolicy;
import com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule.*;
import com.amazonas.common.exceptions.DiscountPolicyException;
import com.amazonas.backend.exceptions.PurchasePolicyException;
import com.amazonas.common.DiscountDTOs.Node;
import com.amazonas.common.DiscountDTOs.Parser;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.PurchaseRuleDTO.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PurchasePolicyTranslator {
    public static PurchaseRuleDTO translator(String cfgText) throws PurchasePolicyException, DiscountPolicyException {
        Node discountTree = Parser.parseCode(cfgText);
        return translateNode(discountTree);
    }

    private static PurchaseRuleDTO translateNode(Node node) throws PurchasePolicyException {
        if (node == null) {
            throw new PurchasePolicyException("Unexpected error11", 0, 0);
        }
        else if (node.isString) {
            throw new PurchasePolicyException("Unexpected error12", 0, 0);
        }
        else {
            if (node.children == null || node.children.length == 0) {
                throw new PurchasePolicyException("Unexpected error13", 0, 0);
            }
            if (node.children[0].isString) {
                switch (node.children[0].content) {
                    case "or", "and" -> {
                        List<PurchaseRuleDTO> discounts = new ArrayList<>();
                        for (int i = 1; i < node.children.length; i++) {
                            discounts.add(translateNode(node.children[i]));
                        }
                        return switch (node.children[0].content) {
                            case "or" ->
                                    new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.OR, discounts);
                            case "and" ->
                                    new MultiplePurchaseRuleDTO(MultiplePurchaseRuleType.AND, discounts);
                            default -> throw new PurchasePolicyException("Unexpected error14", 0, 0);
                        };
                    }
                    case "age-restriction" -> {
                        return new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.AGE_RESTRICTION, Integer.parseInt(node.children[1].content));
                    }
                    case "max-unique-products" -> {
                        return new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS, Integer.parseInt(node.children[1].content));
                    }
                    case "min-unique-products" -> {
                        return new NumericalPurchaseRuleDTO(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS, Integer.parseInt(node.children[1].content));
                    }
                    case "day-restriction" -> {
                        return new DatePurchaseRuleDTO(DatePurchaseRuleType.DAY_RESTRICTION, getDate(node.children[1]), getDate(node.children[2]));
                    }
                    case "hour-restriction" -> {
                        return new DatePurchaseRuleDTO(DatePurchaseRuleType.HOUR_RESTRICTION, getHour(node.children[1]), getHour(node.children[2]));
                    }
                    case "if-check" -> {
                        return new ConditionalPurchaseRuleDTO(nodeToCondition(node.children[1]), translateNode(node.children[2]));
                    }
                    default -> throw new PurchasePolicyException("Unexpected error15", 0, 0);
                }
            }
            else {
                throw  new PurchasePolicyException("Unexpected error16", 0, 0);
            }
        }
    }

    private static ConditionLevelDTO nodeToCondition(Node node) throws PurchasePolicyException {
        if (node == null) {
            throw new PurchasePolicyException("Unexpected error17", 0, 0);
        }
        else if (node.isString) {
            throw new PurchasePolicyException("Unexpected error18", 0, 0);
        }
        else {
            if (node.children == null || node.children.length == 0) {
                throw new PurchasePolicyException("Unexpected error19", 0, 0);
            }
            if (node.children[0].isString) {
                switch (node.children[0].content) {
                    case "product-quantity-more-than" -> {
                        return new ConditionLevelDTO(ConditionLevelType.PRODUCT_LEVEL, node.children[1].content, Integer.parseInt(node.children[2].content));
                    }
                    case "category-quantity-more-than" -> {
                        return new ConditionLevelDTO(ConditionLevelType.CATEGORY_LEVEL, node.children[1].content, Integer.parseInt(node.children[2].content));
                    }
                    default -> throw new PurchasePolicyException("Unexpected error21", 0, 0);
                }
            }
            else {
                throw  new PurchasePolicyException("Unexpected error22", 0, 0);
            }
        }
    }

    //for debugging
    private static void printNode(Node node, int tabs) throws PurchasePolicyException {
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

    private static LocalTime getHour(Node time) throws PurchasePolicyException {
        if (time == null) {
            throw new PurchasePolicyException("Unexpected error22", 0, 0);
        }
        if (!time.isString) {
            throw new PurchasePolicyException("Unexpected error23", 0, 0);
        }
        if (time.content.length() != 4) {
            throw new PurchasePolicyException("hour must be in the form of HHMM", time.start, time.end);
        }
        int hour = Integer.parseInt(time.content.substring(0,2));
        int minute = Integer.parseInt(time.content.substring(2,4));
        if (hour < 0 || hour > 23) {
            throw new PurchasePolicyException("hour must be in range of 0-23", time.start, time.end);
        }
        if (minute < 0 || minute > 59) {
            throw new PurchasePolicyException("minute must be in range of 0-59", time.start, time.end);
        }
        try {
            return LocalTime.of(hour, minute);
        }
        catch (Exception e) {
            throw new PurchasePolicyException("Invalid hour", time.start, time.end);
        }
    }

    private static LocalDate getDate(Node date) throws PurchasePolicyException {
        if (date == null) {
            throw new PurchasePolicyException("Unexpected error22", 0, 0);
        }
        if (!date.isString) {
            throw new PurchasePolicyException("Unexpected error23", 0, 0);
        }
        if (date.content.length() != 8) {
            throw new PurchasePolicyException("hour must be in the form of DDMMYYYY", date.start, date.end);
        }
        int day = Integer.parseInt(date.content.substring(0,2));
        int month = Integer.parseInt(date.content.substring(2,4));
        int year = Integer.parseInt(date.content.substring(4,8));
        try {
            return LocalDate.of(year, month, day);
        }
        catch (Exception e) {
            throw new PurchasePolicyException("Invalid date", date.start, date.end);
        }
    }

    public PurchaseRule translatePurchaseRuleDTO(PurchaseRuleDTO purchaseRuleDTO) throws StoreException {
        switch (purchaseRuleDTO) {
            case null -> throw new StoreException("Discount policy cannot be null");

            case NumericalPurchaseRuleDTO numericalPurchaseRuleDTO -> {
                switch (numericalPurchaseRuleDTO.type()) {
                    case AGE_RESTRICTION -> new AgeRestrictionRule(numericalPurchaseRuleDTO.data());
                    case MIN_UNIQUE_PRODUCTS -> new MinUniqueProductsRule(numericalPurchaseRuleDTO.data());
                    case MAX_UNIQUE_PRODUCTS -> new MaxUniqueProductsRule(numericalPurchaseRuleDTO.data());
                    default ->
                            throw new StoreException("Unknown purchase rule type: " + numericalPurchaseRuleDTO.type().name());
                }
            }
            case MultiplePurchaseRuleDTO multiplePurchaseRuleDTO -> {
                if (multiplePurchaseRuleDTO.purchaseRules() == null || multiplePurchaseRuleDTO.purchaseRules().isEmpty()) {
                    throw new StoreException("Discount policy cannot be empty");
                } else if (multiplePurchaseRuleDTO.type() == null) {
                    throw new StoreException("Discount policy type cannot be empty");
                }
                List<PurchaseRule> purchaseRules = new LinkedList<>();
                for (PurchaseRuleDTO dto : multiplePurchaseRuleDTO.purchaseRules()) {
                    purchaseRules.add(translatePurchaseRuleDTO(dto));
                }
                switch (multiplePurchaseRuleDTO.type()) {
                    case AND -> new AndRule(purchaseRules);
                    case OR -> new OrRule(purchaseRules);
                    default -> throw new StoreException("Unknown purchase rule type: " + multiplePurchaseRuleDTO.type().name());
                }
            }
            case DatePurchaseRuleDTO datePurchaseRuleDTO -> {
                switch (datePurchaseRuleDTO.type()) {
                    case DAY_RESTRICTION -> new DayRestrictionRule((LocalDate)(datePurchaseRuleDTO.beginningRange()), (LocalDate)(datePurchaseRuleDTO.endRange()));
                    case HOUR_RESTRICTION -> new HoursRestrictionRule((LocalTime) (datePurchaseRuleDTO.beginningRange()), (LocalTime) (datePurchaseRuleDTO.endRange()));
                    default -> throw new StoreException("Unknown purchase rule type: " + datePurchaseRuleDTO.type().name());
                }
            }
            case ConditionalPurchaseRuleDTO conditionalPurchaseRuleDTO -> {
                switch (conditionalPurchaseRuleDTO.conditionLevelDTO().type()) {
                    case PRODUCT_LEVEL -> new ConditionalProductRule(conditionalPurchaseRuleDTO.conditionLevelDTO().levelIdentifier(),
                                                                     conditionalPurchaseRuleDTO.conditionLevelDTO().quantity(),
                                                                     translatePurchaseRuleDTO(conditionalPurchaseRuleDTO.purchaseRuleDTO()));
                    case CATEGORY_LEVEL -> new ConditionalCategoryRule(conditionalPurchaseRuleDTO.conditionLevelDTO().levelIdentifier(),
                                                                       conditionalPurchaseRuleDTO.conditionLevelDTO().quantity(),
                                                                       translatePurchaseRuleDTO(conditionalPurchaseRuleDTO.purchaseRuleDTO()));
                }
            }
            default -> throw new StoreException("Invalid discount policy");
        }
        throw new StoreException("Invalid discount policy");
    }
}
