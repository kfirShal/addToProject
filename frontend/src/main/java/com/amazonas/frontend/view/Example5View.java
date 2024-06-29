package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@Route("purchase-discount-policy")
public class Example5View extends BaseLayout {

    private final AppController appController;

    public Example5View(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window title
        String newTitle = "Purchase & Discount Policy";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Create and configure the grid for Purchase policy
        H3 purchasePolicyTitle = new H3("Purchase Policy");
        purchasePolicyTitle.getStyle().set("margin-top", "30px");
        Grid<String> purchasePolicyGrid = new Grid<>();
        // Add logic to populate purchase policy data
        purchasePolicyGrid.setItems("Policy 1", "Policy 2", "Policy 3"); // Replace with actual data
        purchasePolicyGrid.addColumn(policy -> policy);
        Button editPurchasePolicyButton = new Button("Edit", event -> {
            // Add logic to edit purchase policy
        });

        // Create and configure the grid for Discount policy
        H3 discountPolicyTitle = new H3("Discount Policy");
        discountPolicyTitle.getStyle().set("margin-top", "30px");
        Grid<String> discountPolicyGrid = new Grid<>();
        // Add logic to populate discount policy data
        discountPolicyGrid.setItems("Discount 1", "Discount 2", "Discount 3"); // Replace with actual data
        discountPolicyGrid.addColumn(policy -> policy);
        Button editDiscountPolicyButton = new Button("Edit", event -> {
            // Add logic to edit discount policy
        });

        // Add grids and buttons to the content
        VerticalLayout policyLayout = new VerticalLayout();
        policyLayout.add(purchasePolicyTitle, purchasePolicyGrid, editPurchasePolicyButton,
                discountPolicyTitle, discountPolicyGrid, editDiscountPolicyButton);
        content.add(policyLayout);
    }
}
