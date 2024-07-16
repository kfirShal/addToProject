package com.amazonas.frontend.view;

import com.amazonas.common.DiscountDTOs.*;
import com.vaadin.flow.component.treegrid.TreeGrid;

import java.util.List;

public class DiscountRuleTranslator {

    private TreeGrid<Object> treeGrid;

    public DiscountRuleTranslator() {
        treeGrid = new TreeGrid<>(Object.class);
        treeGrid.removeAllColumns();  // Remove default columns
        treeGrid.addHierarchyColumn(this::getNodeLabel).setHeader("Discount Details");
    }

    public TreeGrid<Object> translateToTreeGrid(List<DiscountComponentDTO> discounts) {
        DiscountDataProvider dataProvider = new DiscountDataProvider(discounts);
        treeGrid.setDataProvider(dataProvider);
        return treeGrid;
    }

    public String getNodeLabel(Object node) {
        if (node instanceof MultipleDiscountDTO) {
            MultipleDiscountDTO multipleDiscount = (MultipleDiscountDTO) node;
            String typeDescription = switch (multipleDiscount.multipleDiscountType()) {
                case MAXIMUM_PRICE -> "(MAXIMUM PRICE) - the maximum discount will be applied";
                case MINIMUM_PRICE -> "(MINIMUM PRICE) - the minimum discount will be applied";
                case ADDITION -> "(ADDITION) - all discounts will be added together";
            };
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
            return "Complex Discount: Conditional";
        } else if (node instanceof UnaryConditionDTO) {
            UnaryConditionDTO unaryCondition = (UnaryConditionDTO) node;
            String conditionDescription = switch (unaryCondition.unaryConditionType()) {
                case AT_LEAST_NUMBER_OF_ITEMS_FROM_CATEGORY -> "At least " + unaryCondition.number() + " items from category: " + unaryCondition.text();
                case AT_LEAST_NUMBER_OF_SOME_PRODUCT -> "At least " + unaryCondition.number() + " items of product: " + unaryCondition.text();
                case AT_LEAST_SOME_PRICE -> "Minimum total price: " + unaryCondition.number();
            };
            return "Condition: " + conditionDescription;
        } else if (node instanceof MultipleConditionDTO) {
            MultipleConditionDTO multipleCondition = (MultipleConditionDTO) node;
            String conditionTypeDescription = switch (multipleCondition.multipleConditionType()) {
                case OR -> "(OR) - at least one of the conditions must be met";
                case XOR -> "(XOR) - exactly one of the conditions must be met";
                case AND -> "(AND) - all conditions must be met";
            };
            return "Multiple Conditions: " + conditionTypeDescription;
        }
        return "Unknown Discount Component";
    }
}
