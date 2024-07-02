package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

@Route("Example4View")
public class PurchaseAndDiscount extends BaseLayout {
    private final AppController appController;
    private final List<String> purchasePolicies;
    private final List<String> discountPolicies;

    public PurchaseAndDiscount(AppController appController) {
        super(appController);
        this.appController = appController;

        // Sample policy data
        purchasePolicies = new ArrayList<>();
        purchasePolicies.add("Policy 1");
        purchasePolicies.add("Policy 2");
        purchasePolicies.add("Policy 3");

        discountPolicies = new ArrayList<>();
        discountPolicies.add("Discount 1");
        discountPolicies.add("Discount 2");
        discountPolicies.add("Discount 3");

        // Set the window title
        String newTitle = "Purchase & Discount Policy";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Create and configure the grid for Purchase policy
        H3 purchasePolicyTitle = new H3("Purchase Policy");
        purchasePolicyTitle.getStyle().set("margin-top", "30px");
        Grid<String> purchasePolicyGrid = new Grid<>();
        purchasePolicyGrid.setItems(purchasePolicies);
        purchasePolicyGrid.addColumn(policy -> policy).setHeader("Policy");
        purchasePolicyGrid.addComponentColumn(policy -> {
            Button removeButton = new Button("Remove", event -> {
                purchasePolicies.remove(policy);
                purchasePolicyGrid.setItems(purchasePolicies);
            });
            return removeButton;
        });

        Button editPurchasePolicyButton = new Button("Add Policy", event -> {
            // Add logic to edit purchase policy
        });

        // Create and configure the grid for Discount policy
        H3 discountPolicyTitle = new H3("Discount Policy");
        discountPolicyTitle.getStyle().set("margin-top", "30px");
        Grid<String> discountPolicyGrid = new Grid<>();
        discountPolicyGrid.setItems(discountPolicies);
        discountPolicyGrid.addColumn(policy -> policy).setHeader("Discount");
        discountPolicyGrid.addComponentColumn(policy -> {
            Button removeButton = new Button("Remove", event -> {
                discountPolicies.remove(policy);
                discountPolicyGrid.setItems(discountPolicies);
            });
            return removeButton;
        });

        Button editDiscountPolicyButton = new Button("Add Policy", event -> {
            // Add logic to edit discount policy
        });

        // Add grids and buttons to the content
        VerticalLayout policyLayout = new VerticalLayout();
        policyLayout.add(purchasePolicyTitle, purchasePolicyGrid, editPurchasePolicyButton,
                discountPolicyTitle, discountPolicyGrid, editDiscountPolicyButton);
        content.add(policyLayout);
    }
}
