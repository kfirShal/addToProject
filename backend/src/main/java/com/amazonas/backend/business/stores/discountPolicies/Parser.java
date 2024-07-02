package com.amazonas.backend.business.stores.discountPolicies;

import com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.backend.exceptions.StoreException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Parser {

    public static Node parseCode(String code) throws DiscountPolicyException {
        return parse(code, 0);
    }

    private static Node parse(String input, int offset) throws DiscountPolicyException {
        if (input == null) {
            throw new DiscountPolicyException("Input is invalid", 0, 0);
        }
        if (input.isEmpty()) {
            throw new DiscountPolicyException("Excpected discount", offset, 0);
        }
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= 20) {
                continue;
            }
            else {
                if (input.charAt(i) == '(') {
                    for (int index = i + 1; index < input.length(); index++) {
                        if (input.charAt(index) <= 20) {
                            continue;
                        }
                        if (input.startsWith("minimal-price", index)) {
                            return parseMultipleDiscounts("minimal-price", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("maximal-price", index)) {
                            return parseMultipleDiscounts("maximal-price", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("add", index)) {
                            return parseMultipleDiscounts("add", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("global-discount", index)) {
                            return parseWordNumber("global-discount", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("product-discount", index)) {
                            return parseWordNumberNumber("product-discount", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("category-discount", index)) {
                            return parseWordWordNumber("category-discount", input.substring(index), offset+index);
                        }
                        else if (input.startsWith("if-then", index)) {
                            return parseIfThen(input.substring(index), offset+index);
                        }
                        else {
                            throw new DiscountPolicyException("cannot recognize discount", offset + i, offset + input.length());
                        }
                    }

                }
                else {
                    throw new DiscountPolicyException("Syntax error", offset + i, offset + input.length());
                }
            }
        }
        throw new DiscountPolicyException("Cannot recognize discount policy", offset, offset + input.length());
    }

    private static Node parseMultipleDiscounts(String type, String input, int offset) throws DiscountPolicyException {
        if (!input.startsWith(type)) {
            throw new DiscountPolicyException("Unexpected error", 0, 0);
        }
        List<Node> discounts = new ArrayList<Node>();
        discounts.add(new Node(type, offset, offset+type.length()));
        Node firstDiscount = parse(input.substring(type.length()), offset + type.length());
        discounts.add(firstDiscount);
        int index = firstDiscount.end - offset;
        while (index < input.length()) {
            if (input.charAt(index) <= 20) {
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
                throw new DiscountPolicyException("Syntax error", offset + index, offset + input.length());
            }
        }
        throw new DiscountPolicyException("Missing ')' or another discount", offset + type.length(), offset + input.length());
    }

    private static Node parseWordNumber(String type, String input, int offset) throws DiscountPolicyException {
        if (!input.startsWith(type)) {
            throw new DiscountPolicyException("Unexpected error", 0, 0);
        }
        List<Node> ret = new LinkedList<>();
        ret.add(new Node("type", offset, type.length()));
        ret.add(getNumber(input.substring(type.length()), offset + type.length()));
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 20) {
                continue;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[ret.size()];
                ret.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new DiscountPolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
            }
        }
        throw new DiscountPolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
    }

    private static Node parseWordNumberNumber(String type, String input, int offset) throws DiscountPolicyException {
        if (!input.startsWith(type)) {
            throw new DiscountPolicyException("Unexpected error", 0, 0);
        }
        List<Node> ret = new LinkedList<>();
        ret.add(new Node(type, offset, type.length()));
        ret.add(getNumber(input.substring(type.length()), offset + type.length()));
        ret.add(getNumber(input.substring(ret.getLast().end + 1 - offset), ret.getLast().end + 1));
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 20) {
                continue;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[ret.size()];
                ret.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new DiscountPolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
            }
        }
        throw new DiscountPolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
    }

    private static Node parseWordWordNumber(String type, String input, int offset) throws DiscountPolicyException {
        if (!input.startsWith(type)) {
            throw new DiscountPolicyException("Unexpected error", 0, 0);
        }
        List<Node> ret = new LinkedList<>();
        ret.add(new Node(type, offset, offset + type.length()));
        int first = -1;
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 20) {
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
            throw new DiscountPolicyException("Missing argument", ret.getLast().end + 1, offset + input.length());
        }
        ret.add(getNumber(input.substring(ret.getLast().end + 1 - offset), ret.getLast().end + 1));
        for (int index = ret.getLast().end + 1 - offset; index < input.length(); index++) {
            if (input.charAt(index) <= 20) {
                continue;
            }
            else if (input.startsWith(")", index)) {
                Node[] discountsArray = new Node[ret.size()];
                ret.toArray(discountsArray);
                return new Node(discountsArray, offset, offset + index +1);
            }
            else {
                throw new DiscountPolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
            }
        }
        throw new DiscountPolicyException("Missing ')'", ret.getLast().end + 1, offset + input.length());
    }

    private static Node parseIfThen(String input, int offset) throws DiscountPolicyException {
        if (!input.startsWith("if-then")) {
            throw new DiscountPolicyException("Unexpected error", 0, 0);
        }
        Node[] ret = new Node[2];
        ret[0] = parseCondition(input.substring(7), offset + 7);
        ret [1] = parse(input.substring(ret[0].end + 1), ret[0].end + 1);
        return new Node(ret, offset, ret[1].end);
    }

    private static Node parseCondition(String input, int offset) throws DiscountPolicyException {
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= 20) {
                continue;
            } else {
                if (input.charAt(i) == '(') {
                    for (int index = i + 1; index < input.length(); index++) {
                        if (input.charAt(index) <= 20) {
                            continue;
                        }
                        if (input.startsWith("price-over", index)) {
                            return parseWordNumber("price-over", input.substring(index), offset + index);
                        } else if (input.startsWith("product-quantity-more-than", index)) {
                            return parseWordNumberNumber("product-quantity-more-than", input.substring(index), offset + index);
                        } else if (input.startsWith("category-quantity-more-than ", index)) {
                            return parseWordWordNumber("category-quantity-more-than ", input.substring(index), offset + index);
                        } else if (input.startsWith("&", index)) {
                            return parseMultipleConditions("&", input.substring(index), offset + index);
                        } else if (input.startsWith("|", index)) {
                            return parseMultipleConditions("|", input.substring(index), offset + index);
                        } else if (input.startsWith("^", index)) {
                            return parseMultipleConditions("^", input.substring(index), offset + index);
                        } else {
                            throw new DiscountPolicyException("cannot recognize discount", offset + i, offset + input.length());
                        }
                    }

                } else {
                    throw new DiscountPolicyException("Syntax error", offset + i, offset + input.length());
                }
            }
        }
        throw new DiscountPolicyException("cannot recognize discount condition", offset, offset + input.length());
    }

    private static Node parseMultipleConditions(String type, String input, int offset) throws DiscountPolicyException {
        if (!input.startsWith(type)) {
            throw new DiscountPolicyException("Unexpected error", 0, 0);
        }
        List<Node> discounts = new ArrayList<Node>();
        discounts.add(new Node(type, offset, offset+type.length()));
        Node firstDiscount = parseCondition(input.substring(type.length()), offset + type.length());
        discounts.add(firstDiscount);
        int index = firstDiscount.end - offset;
        while (index < input.length()) {
            if (input.charAt(index) <= 20) {
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
                throw new DiscountPolicyException("Syntax error", offset + index, offset + input.length());
            }
        }
        throw new DiscountPolicyException("Missing ')' or another condition", offset + type.length(), offset + input.length());
    }

    private static Node getNumber(String input, int offset) throws DiscountPolicyException {
        int first = -1;
        boolean found = false;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) <= 20) {
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
                    throw new DiscountPolicyException("Excepted number", offset + first, offset + input.length() - first - 1);
                }
                else {
                    throw new DiscountPolicyException("Excepted number", offset + i, offset + input.length() - i - 1);
                }
            }
        }
        throw new DiscountPolicyException("Excepted number", offset, offset + input.length()- 1);
    }
}
