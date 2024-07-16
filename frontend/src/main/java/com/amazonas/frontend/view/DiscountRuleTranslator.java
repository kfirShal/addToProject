package com.amazonas.frontend.view;

import com.amazonas.common.DiscountDTOs.*;
import com.vaadin.flow.component.treegrid.TreeGrid;

import java.util.ArrayList;
import java.util.List;

public class DiscountRuleTranslator {

    private TreeGrid<DiscountComponentDTO> treeGrid;

    public DiscountRuleTranslator() {
        treeGrid = new TreeGrid<>(DiscountComponentDTO.class);
        treeGrid.removeAllColumns();  // Remove default columns
        treeGrid.addHierarchyColumn(this::getNodeLabel).setHeader("Discount Policy Details");
    }

    public TreeGrid<DiscountComponentDTO> translateToTreeGrid(List<DiscountComponentDTO> discounts) {
        treeGrid.setItems(discounts, this::getChildNodes);
        return treeGrid;
    }

    public String getNodeLabel(DiscountComponentDTO node) {
        if (node instanceof MultipleDiscountDTO) {
            MultipleDiscountDTO multipleDiscount = (MultipleDiscountDTO) node;
            String typeDescription = "";
            switch (multipleDiscount.multipleDiscountType()) {
                case MAXIMUM_PRICE -> typeDescription = "(MAXIMUM PRICE) - the maximum discount will be applied";
                case MINIMUM_PRICE -> typeDescription = "(MINIMUM PRICE) - the minimum discount will be applied";
                case ADDITION -> typeDescription = "(ADDITION) - all discounts will be added together";
            }
            return "Multiple Discounts: " + typeDescription;
        } else if (node instanceof SimpleDiscountDTO) {
            SimpleDiscountDTO simpleDiscount = (SimpleDiscountDTO) node;
            String levelDescription = switch (simpleDiscount.hierarchyLevel()) {
                case StoreLevel -> "Store Level Discount";
                case CategoryLevel -> "Category Level Discount: " + simpleDiscount.text();
                case ProductLevel -> "Product Level Discount: " + simpleDiscount.text();
            };
            return levelDescription + " - " + simpleDiscount.percent() + "% off";
        } else if (node instanceof ComplexDiscountDTO) {
            if (((ComplexDiscountDTO) node).discountCondition() instanceof UnaryConditionDTO) {
                UnaryConditionDTO unaryCondition = (UnaryConditionDTO) ((ComplexDiscountDTO) node).discountCondition();
                String conditionDescription = switch (unaryCondition.unaryConditionType()) {
                    case AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY -> "At least " + unaryCondition.number() + " items from category: " + unaryCondition.text();
                    case AT_LEAST_NUMBER_OF_SOME_PRODUCT -> "At least " + unaryCondition.number() + " items of product: " + unaryCondition.text();
                    case AT_LEAST_SOME_PRICE -> "Minimum total price: " + unaryCondition.number();
                };
                return "Condition: " + conditionDescription;
            } else if (((ComplexDiscountDTO) node).discountCondition() instanceof MultipleConditionDTO) {
                MultipleConditionDTO multipleCondition = (MultipleConditionDTO) ((ComplexDiscountDTO) node).discountCondition();
                String conditionTypeDescription = switch (multipleCondition.multipleConditionType()) {
                    case OR -> "(OR) - at least one of the conditions must be met";
                    case XOR -> "(XOR) - exactly one of the conditions must be met";
                    case AND -> "(AND) - all conditions must be met";
                };

            return "Complex Discount: Conditional";
        }
        }
        return "Unknown Discount Component";
    }

    public List<DiscountComponentDTO> getChildNodes(DiscountComponentDTO parent) {
        if (parent instanceof MultipleDiscountDTO) {
            return ((MultipleDiscountDTO) parent).discountComponents();
        } else if (parent instanceof ComplexDiscountDTO) {
            List<DiscountComponentDTO> children = new ArrayList<>();
            children.add(((ComplexDiscountDTO) parent).discountCondition());
            children.add(((ComplexDiscountDTO) parent).discountComponentDTO());
            return children;
        } else if (parent instanceof MultipleConditionDTO) {
            return ((MultipleConditionDTO) parent).conditions();
        }
        return new ArrayList<>();
    }
}
