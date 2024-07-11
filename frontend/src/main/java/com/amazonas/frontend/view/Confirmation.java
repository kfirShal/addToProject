package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

@Route("Confirmation")
public class Confirmation extends Profile {

    public Confirmation(AppController appController) {
        super(appController);
        createConfirmationPage(appController);
    }

    private void createConfirmationPage(AppController appController) {
        // Create a layout for the confirmation page
        VerticalLayout layout = new VerticalLayout();
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Add a confirmation message
        Span confirmationMessage = new Span("Your order has been confirmed!");
        confirmationMessage.getStyle().set("font-size", "20px");
        confirmationMessage.getStyle().set("font-weight", "bold");

        Span orderDetails = new Span();

        // Add a button to go back to the home page
        Button homeButton = new Button("Go to Home");
        homeButton.addClickListener(_ -> getUI().ifPresent(ui -> ui.navigate("")));

        // Add components to the layout
        layout.add(confirmationMessage, orderDetails, homeButton);

        // Add the layout to the main view
        content.add(layout);
    }
}
