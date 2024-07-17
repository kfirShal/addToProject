package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;

import java.util.List;

import static com.amazonas.frontend.control.AppController.isUserLoggedIn;

@Route("Payment")
public class Payment extends Cart {

    private final AppController appController;

    public Payment(AppController appController) {
        super(appController);
        this.appController = appController;

        if (!isUserLoggedIn()) {
            UI.getCurrent().navigate("");
            return;
        }

        // Add payment form
        FormLayout paymentForm = getPaymentForm();

        // Add payment summary
        VerticalLayout paymentSummary = new VerticalLayout();
        paymentSummary.add(new Paragraph("Payment Summary"));

        Grid<Product> summaryGrid = new Grid<>(Product.class, false);


        paymentSummary.add(summaryGrid);

        // Add confirm payment button
        Button confirmPaymentButton = new Button("Confirm Payment", _ -> processPayment());
        confirmPaymentButton.getStyle().set("margin-top", "20px");

        // Add everything to the main layout
        content.add(paymentForm, paymentSummary, confirmPaymentButton);
    }

    private static FormLayout getPaymentForm() {
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
        return paymentForm;
    }

    private void processPayment() {
        try {
            List<Transaction> before = appController.postByEndpoint(Endpoints.GET_USER_TRANSACTION_HISTORY, AppController.getCurrentUserId());
            appController.postByEndpoint(Endpoints.START_PURCHASE, null);
            appController.postByEndpoint(Endpoints.PAY_FOR_PURCHASE, null);
            // get the transactionID and go to orders?orderId=transactionID
            List<Transaction> after = appController.postByEndpoint(Endpoints.GET_USER_TRANSACTION_HISTORY, AppController.getCurrentUserId());
            Transaction newTransaction = after.stream().filter(t -> !before.contains(t)).findFirst().orElse(null);
            assert newTransaction != null;
            UI.getCurrent().navigate("Orders?orderId=" + newTransaction.getTransactionId());
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }

    }
}
