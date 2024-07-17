package com.amazonas.backend.business.stores.purchasePolicy;

import com.amazonas.backend.exceptions.PurchasePolicyException;
import com.amazonas.backend.business.stores.discountPolicies.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PurchasePolicyParser {
    public static Node parseCode(String code) throws PurchasePolicyException {
        return parse(code, 0);
    }

    private static Node parse(String input, int offset) throws PurchasePolicyException {
        if (input == null) {
            throw new PurchasePolicyException("Input is invalid", 0, 0);
        }
        if (input.isEmpty()) {
            throw new PurchasePolicyException("Expected purchase policy", offset, 0);
        }
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= 32) {
                continue;
            }
            else {
                if (input.charAt(i) == '(') {
                    for (int index = i + 1; index < input.length(); index++) {
                        if (input.charAt(index) <= 32) {
                            continue;
                        }
                        if (input.startsWith("or", index)) {
                            return parseMultipleDiscounts("or", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("and", index)) {
                            return parseMultipleDiscounts("and", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("age-restriction", index)) {
                            return parseWordNumber("age-restriction ", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("min-unique-products", index)) {
                            return parseWordNumber("min-unique-products ", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("max-unique-products", index)) {
                            return parseWordNumber("max-unique-products ", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("hour-restriction", index)) {
                            return parseWordNumberNumber("hour-restriction", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("if-check", index)) {
                            return parseIfCheck(input.substring(index), offset+index);
                        }
                        else {
                            throw new PurchasePolicyException("cannot recognize discount", offset + i, offset + input.length());
                        }
                    }

                }
                else {
                    throw new PurchasePolicyException("Syntax error" + input, offset + i, offset + input.length());
                }
            }
        }
        throw new PurchasePolicyException("Cannot recognize discount policy", offset, offset + input.length());
    }

    private static Node parseMultipleDiscounts(String type, String input, int offset) throws PurchasePolicyException {
        if (!input.startsWith(type)) {
            throw new PurchasePolicyException("Unexpected error1", 0, 0);
        }
        List<Node> discounts = new ArrayList<Node>();
        discounts.add(new Node(type, offset, offset+type.length()));
        Node firstDiscount = parse(input.substring(type.length()), offset + type.length());
        discounts.add(firstDiscount);
        int index = firstDiscount.end - offset;
        while (index < input.length()) {
            if (input.charAt(index) <= 32) {
                index++;
                continue;
            }
            else if (input.startsWith("(", index)) {
                discounts.add(parse(input.substring(index), offset+index));
                index = discounts.getLast().end;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[discounts.size()];
                discounts.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new PurchasePolicyException("Syntax error", offset + index, offset + input.length());
            }
        }
        throw new PurchasePolicyException("Missing ')' or another discount", offset + type.length(), offset + input.length());
    }

    private static Node parseWordNumber(String type, String input, int offset) throws PurchasePolicyException {
        if (!input.startsWith(type)) {
            throw new PurchasePolicyException("Unexpected error2", 0, 0);
        }
        List<Node> ret = new LinkedList<>();
        ret.add(new Node(type, offset, type.length()));
        ret.add(getNumber(input.substring(type.length()), offset + type.length()));
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 32) {
                continue;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[ret.size()];
                ret.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new PurchasePolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
            }
        }
        throw new PurchasePolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
    }

    private static Node parseWordNumberNumber(String type, String input, int offset) throws PurchasePolicyException {
        if (!input.startsWith(type)) {
            throw new PurchasePolicyException("Unexpected error3", 0, 0);
        }
        List<Node> ret = new LinkedList<>();
        ret.add(new Node(type, offset, type.length()));
        ret.add(getNumber(input.substring(type.length()), offset + type.length()));
        ret.add(getNumber(input.substring(ret.getLast().end + 1 - offset), ret.getLast().end + 1));
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 32) {
                continue;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[ret.size()];
                ret.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new PurchasePolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
            }
        }
        throw new PurchasePolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
    }

    private static Node parseWordWordNumber(String type, String input, int offset) throws PurchasePolicyException {
        if (!input.startsWith(type)) {
            throw new PurchasePolicyException("Unexpected error4", 0, 0);
        }
        List<Node> ret = new LinkedList<>();
        ret.add(new Node(type, offset, offset + type.length()));
        int first = -1;
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 32) {
                if (first != -1) {
                    ret.add(new Node(input.substring(first, index), offset + first, offset + index - 1));
                    break;
                }
            }
            else if ((input.charAt(index) >= 65 && input.charAt(index) <= 90) ||
                    (input.charAt(index) >= 97 && input.charAt(index) <= 122) ||
                    (input.charAt(index) >= 48 && input.charAt(index) <= 57)) {
                if (first == -1) {
                    first = index;
                }
            }
        }
        if (first == -1) {
            throw new PurchasePolicyException("Missing argument", ret.getLast().end + 1, offset + input.length());
        }
        ret.add(getNumber(input.substring(ret.getLast().end + 1 - offset), ret.getLast().end + 1));
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 32) {
                continue;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[ret.size()];
                ret.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new PurchasePolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
            }
        }
        throw new PurchasePolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
    }

    private static Node parseIfCheck(String input, int offset) throws PurchasePolicyException {
        if (!input.startsWith("if-then")) {
            throw new PurchasePolicyException("Unexpected error5", 0, 0);
        }
        Node[] ret = new Node[3];
        ret[0] = new Node("if-then", offset, offset+7);
        ret[1] = parseCondition(input.substring(7), offset + 7);
        ret [2] = parse(input.substring(ret[1].end - offset), ret[1].end);
        return new Node(ret, offset, ret[2].end);
    }

    private static Node parseCondition(String input, int offset) throws PurchasePolicyException {
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= 32) {
                continue;
            } else {
                if (input.charAt(i) == '(') {
                    for (int index = i + 1; index < input.length(); index++) {
                        if (input.charAt(index) <= 32) {
                            continue;
                        }
                        if (input.startsWith("product-quantity-more-than", index)) {
                            return parseWordWordNumber("product-quantity-more-than", input.substring(index), offset + index);
                        } else if (input.startsWith("category-quantity-more-than", index)) {
                            return parseWordWordNumber("category-quantity-more-than", input.substring(index), offset + index);
                        } else {
                            throw new PurchasePolicyException("cannot recognize discount", offset + i, offset + input.length());
                        }
                    }
                } else {
                    throw new PurchasePolicyException("Syntax error", offset + i, offset + input.length());
                }
            }
        }
        throw new PurchasePolicyException("cannot recognize discount condition", offset, offset + input.length());
    }

    private static Node parseMultipleConditions(String type, String input, int offset) throws PurchasePolicyException {
        if (!input.startsWith(type)) {
            throw new PurchasePolicyException("Unexpected error6", 0, 0);
        }
        List<Node> discounts = new ArrayList<Node>();
        discounts.add(new Node(type, offset, offset+type.length()));
        Node firstDiscount = parseCondition(input.substring(type.length()), offset + type.length());
        discounts.add(firstDiscount);
        int index = firstDiscount.end - offset;
        while (index < input.length()) {
            if (input.charAt(index) <= 32) {
                index++;
                continue;
            }
            else if (input.startsWith("(", index)) {
                discounts.add(parseCondition(input.substring(index), offset+index));
                index = discounts.getLast().end;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[discounts.size()];
                discounts.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new PurchasePolicyException("Syntax error", offset + index, offset + input.length());
            }
        }
        throw new PurchasePolicyException("Missing ')' or another condition", offset + type.length(), offset + input.length());
    }

    private static Node getNumber(String input, int offset) throws PurchasePolicyException {
        int first = -1;
        boolean found = false;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= 32 || input.charAt(i) == ')') {
                if (found) {
                    return new Node(input.substring(first, i), offset + first, offset + i - 1);
                }
            }
            else if (input.charAt(i) >= 48 && input.charAt(i) <= 57) {
                if (!found) {
                    found = true;
                    first = i;
                }
            }
            else {
                if (found) {
                    throw new PurchasePolicyException("Excepted number", offset + first, offset + input.length() - first - 1);
                }
                else {
                    throw new PurchasePolicyException("Excepted number", offset + i, offset + input.length() - i - 1);
                }
            }
        }
        throw new PurchasePolicyException("Excepted number", offset, offset + input.length()- 1);
    }
}
