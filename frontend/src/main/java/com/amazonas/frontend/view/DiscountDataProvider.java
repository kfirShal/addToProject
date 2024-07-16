package com.amazonas.frontend.view;

import com.amazonas.common.DiscountDTOs.*;
import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DiscountDataProvider extends AbstractBackEndHierarchicalDataProvider<Object, Void> {

    private final List<DiscountComponentDTO> rootItems;

    public DiscountDataProvider(List<DiscountComponentDTO> rootItems) {
        this.rootItems = rootItems;
    }

    @Override
    public int getChildCount(HierarchicalQuery<Object, Void> query) {
        Object parent = query.getParent();
        if (parent == null) {
            return rootItems.size();
        } else {
            return getChildNodes(parent).size();
        }
    }

    @Override
    public boolean hasChildren(Object item) {
        return !getChildNodes(item).isEmpty();
    }

    @Override
    protected Stream<Object> fetchChildrenFromBackEnd(HierarchicalQuery<Object, Void> query) {
        Object parent = query.getParent();
        if (parent == null) {
            return rootItems.stream().map(Object.class::cast);
        } else {
            return getChildNodes(parent).stream().map(Object.class::cast);
        }
    }

    private List<Object> getChildNodes(Object parent) {
        List<Object> children = new ArrayList<>();

        if (parent instanceof MultipleDiscountDTO) {
            children.addAll(((MultipleDiscountDTO) parent).discountComponents());
        } else if (parent instanceof ComplexDiscountDTO) {
            children.add(((ComplexDiscountDTO) parent).discountCondition());
            children.add(((ComplexDiscountDTO) parent).discountComponentDTO());
        } else if (parent instanceof MultipleConditionDTO) {
            children.addAll(((MultipleConditionDTO) parent).conditions());
        }

        return children;
    }
}
