package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("SystemManagementView")
public class SystemManagementView extends BaseLayout {

    private final AppController appController;

    public SystemManagementView(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window title
        String newTitle = "System Management";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);

        Button shutdownButton = new Button("Shutdown System", click -> {
            // Add logic to shutdown the system
        });

        // Create buttons for starting and shutting down the system
        Button startButton = new Button("Start System", click -> {
            // Add logic to start the system
        });

        // Styling for the buttons and their layout
        shutdownButton.getStyle().set("background-color", "red");
        shutdownButton.getStyle().set("color", "white"); // Set text color to white
        startButton.getStyle().set("background-color", "green");
        startButton.getStyle().set("color", "white"); // Set text color to white
        HorizontalLayout buttonsLayout = new HorizontalLayout(shutdownButton, startButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER); // Center align the buttons
        content.add(buttonsLayout);

        // Shipping Companies Grid
        H3 shippingTitle = new H3("Shipping Companies");
        shippingTitle.getStyle().set("margin-top", "30px");
        Grid<String> shippingGrid = new Grid<>();
        shippingGrid.addColumn(name -> name).setHeader("Company Name");

        // Remove button for shipping grid
        shippingGrid.addComponentColumn(name -> {
            Button removeButton = new Button("Remove", click -> {
                // Add logic to remove the company (name) from the grid
            });
            return removeButton;
        });

        Button addShippingButton = new Button("Add", click -> {
            // Add logic to add a new shipping company
            shippingGrid.setItems("New Shipping Company"); // Example of adding a new item
        });

        VerticalLayout shippingLayout = new VerticalLayout(shippingTitle, shippingGrid, addShippingButton);
        content.add(shippingLayout);

        // Payment Companies Grid
        H3 paymentTitle = new H3("Payment Companies");
        paymentTitle.getStyle().set("margin-top", "30px");
        Grid<String> paymentGrid = new Grid<>();
        paymentGrid.addColumn(name -> name).setHeader("Company Name");

        // Remove button for payment grid
        paymentGrid.addComponentColumn(name -> {
            Button removeButton = new Button("Remove", click -> {
                // Add logic to remove the company (name) from the grid
            });
            return removeButton;
        });

        Button addPaymentButton = new Button("Add", click -> {
            // Add logic to add a new payment company
            paymentGrid.setItems("New Payment Company"); // Example of adding a new item
        });

        VerticalLayout paymentLayout = new VerticalLayout(paymentTitle, paymentGrid, addPaymentButton);
        content.add(paymentLayout);
    }
}
