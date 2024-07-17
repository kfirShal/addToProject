package com.amazonas.frontend.view;

import com.amazonas.common.PurchaseRuleDTO.*;
import com.vaadin.flow.component.treegrid.TreeGrid;

import java.util.ArrayList;
import java.util.List;

public class PurchaseRuleTranslator {

    private TreeGrid<PurchaseRuleDTO> treeGrid;

    public PurchaseRuleTranslator() {

        treeGrid = new TreeGrid<>(PurchaseRuleDTO.class);
        treeGrid.removeAllColumns();  // Remove default columns
        treeGrid.addHierarchyColumn(this::getNodeLabel).setHeader("Policy Details");
    }

    public TreeGrid<PurchaseRuleDTO> translateToTreeGrid(List<PurchaseRuleDTO> rules) {
        treeGrid.setItems(rules, this::getChildNodes);
        return treeGrid;

    }

    public String getNodeLabel(PurchaseRuleDTO node) {
        if (node instanceof MultiplePurchaseRuleDTO) {
            MultiplePurchaseRuleDTO multipleRule = (MultiplePurchaseRuleDTO) node;
            String s ="";
            if(multipleRule.type().equals(MultiplePurchaseRuleType.AND)){
                 s = "(AND) - all the rules must be followed";
            }
            else if(multipleRule.type().equals(MultiplePurchaseRuleType.OR)){
                s = "(OR) - at least one of the rules must be followed";
            }
            return "Multiple Rules:" + s;
        } else if (node instanceof NumericalPurchaseRuleDTO) {
            NumericalPurchaseRuleDTO numericalRule = (NumericalPurchaseRuleDTO) node;

            if(numericalRule.type().equals(NumericalPurchaseRuleType.AGE_RESTRICTION)){
                return "Age Restriction: " + numericalRule.data();
            }
            else if(numericalRule.type().equals(NumericalPurchaseRuleType.MAX_UNIQUE_PRODUCTS)){
                return "The Customer can buy max "+ numericalRule.data() + " unique products from the store";
            }
            else if(numericalRule.type().equals(NumericalPurchaseRuleType.MIN_UNIQUE_PRODUCTS)){
                return "The Customer need to buy at least "+ numericalRule.data() + " unique products from the store";
            }

        } else if (node instanceof DatePurchaseRuleDTO) {
            DatePurchaseRuleDTO dateRule = (DatePurchaseRuleDTO) node;
            if (dateRule.type().equals(DatePurchaseRuleType.DAY_RESTRICTION)) {
                return "Day restriction from "+ dateRule.beginningRange() + " to " + dateRule.endRange();
            }
            else if(dateRule.type().equals(DatePurchaseRuleType.HOUR_RESTRICTION)){
                return "Hour restriction from " +  dateRule.beginningRange() + " to " + dateRule.endRange();
            }
        } else if (node instanceof ConditionalPurchaseRuleDTO) {
            //ConditionalPurchaseRuleDTO conditionalRule = (ConditionalPurchaseRuleDTO) node;
            return "Conditional Rule:";
        } else if (node instanceof ConditionLevelDTO) {
            ConditionLevelDTO conditionLevel = (ConditionLevelDTO) node;
            if(conditionLevel.type().equals(ConditionLevelType.PRODUCT_LEVEL)){
                return "Condition is on products level." + " Product details: " +conditionLevel.levelIdentifier() + ", Quantity: " + conditionLevel.quantity();
            }
            if(conditionLevel.type().equals(ConditionLevelType.CATEGORY_LEVEL)){
                return "Condition is on category level." + " Category details: " +conditionLevel.levelIdentifier() + ", Quantity: " + conditionLevel.quantity();
            }
        }

        return "Unknown Rule";
    }

    public List<PurchaseRuleDTO> getChildNodes(PurchaseRuleDTO parent) {
        if (parent instanceof MultiplePurchaseRuleDTO) {
            return ((MultiplePurchaseRuleDTO) parent).purchaseRules();
        } else if (parent instanceof ConditionalPurchaseRuleDTO) {
            List<PurchaseRuleDTO> children = new ArrayList<>();
            children.add(((ConditionalPurchaseRuleDTO) parent).conditionLevelDTO());
            children.add(((ConditionalPurchaseRuleDTO) parent).purchaseRuleDTO());
            return children;
        }
        return new ArrayList<>();
    }
}
