package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;

@Route("Payment")
public class Payment extends Cart {

    public Payment(AppController appController) {
        super(appController);

        // check if user logged in, if not return to home page
        returnToMainIfNotLogged();

        // Add payment form
        FormLayout paymentForm = new FormLayout();

        // Add fields for payment details

        NumberField cardNumberField = new NumberField("Card Number");
        cardNumberField.setRequiredIndicatorVisible(true);
        cardNumberField.setMin(1000000000000000L);
        cardNumberField.setMax(9999999999999999L);

        PasswordField cardCvvField = new PasswordField("CVV");
        cardCvvField.setRequired(true);
        cardCvvField.setMinLength(3);
        cardCvvField.setMaxLength(4);

        NumberField cardExpMonthField = new NumberField("Expiry Month");
        cardExpMonthField.setRequiredIndicatorVisible(true);
        cardExpMonthField.setMin(1);
        cardExpMonthField.setMax(12);

        NumberField cardExpYearField = new NumberField("Expiry Year");
        cardExpYearField.setRequiredIndicatorVisible(true);
        cardExpYearField.setMin(2021);  // Ensure the year is current or future year
        cardExpYearField.setMax(2100);

        // Add fields to the form layout
        paymentForm.add(cardNumberField, cardCvvField, cardExpMonthField, cardExpYearField);

        // Add payment summary
        VerticalLayout paymentSummary = new VerticalLayout();
        paymentSummary.add(new Paragraph("Payment Summary"));

        Grid<Product> summaryGrid = new Grid<>(Product.class, false);


        paymentSummary.add(summaryGrid);

        // Add confirm payment button
        Button confirmPaymentButton = new Button("Confirm Payment", e -> processPayment());
        confirmPaymentButton.getStyle().set("margin-top", "20px");

        // Add everything to the main layout
        content.add(paymentForm, paymentSummary, confirmPaymentButton);
    }

    private void processPayment() {
        // Payment processing logic here
        // This is where you would integrate with a payment gateway
        // For this example, we'll simply show a success message
        UI.getCurrent().navigate("Confirmation");
    }
}
