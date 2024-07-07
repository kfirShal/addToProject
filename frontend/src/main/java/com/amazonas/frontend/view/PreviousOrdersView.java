package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Route("previous-orders")
public class PreviousOrdersView extends BaseLayout{
    private final AppController appController;
    private VirtualList<Transaction> transactionsList;
    private List<Transaction> transactions;

    public PreviousOrdersView(AppController appController) {
        super(appController);
        this.appController = appController;
        String message = appController.getPreviousOrdersMessage();
        H2 h1 = new H2(message);
        h1.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        content.add(h1);

        // Initialize transactions list
        transactions = new ArrayList<>();

        try{

            transactions = appController.postByEndpoint(Endpoints.GET_USER_TRANSACTION_HISTORY, null);
        }
        catch (Exception e){
            openErrorDialog(e.getMessage());
        }
        if (transactions == null || transactions.isEmpty()) {
            VerticalLayout noTransactionsLayout = new VerticalLayout();
            noTransactionsLayout.setWidthFull();
            noTransactionsLayout.setHeightFull();
            noTransactionsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            noTransactionsLayout.setAlignItems(FlexComponent.Alignment.CENTER);

            Span noNotificationsMessage = new Span("There are no previous transactions.");
            noNotificationsMessage.getStyle().set("font-size", "24px");
            noNotificationsMessage.getStyle().set("color", "var(--lumo-secondary-text-color)");

            noTransactionsLayout.add(noNotificationsMessage);
            content.add(noTransactionsLayout);
            return;
        }

        // Transaction 1
        Map<Product, Integer> products1 = new HashMap<>();
        products1.put(new Product("P001", "Laptop", 1000.00, "Electronics", "A high-performance laptop.", Rating.FIVE_STARS, "STORE001"), 1);
        products1.put(new Product("P002", "Mouse", 25.00, "Accessories", "A wireless mouse.", Rating.FOUR_STARS, "STORE001"), 2);
        transactions.add(new Transaction("TXN001", "STORE001", "USER001", LocalDateTime.of(2023, 7, 1, 15, 30), products1));

        // Transaction 2
        Map<Product, Integer> products2 = new HashMap<>();
        products2.put(new Product("P003", "Smartphone", 800.00, "Electronics", "A latest model smartphone.", Rating.FIVE_STARS, "STORE002"), 1);
        products2.put(new Product("P004", "Headphones", 50.00, "Accessories", "Noise-cancelling headphones.", Rating.FIVE_STARS, "STORE002"), 1);
        transactions.add(new Transaction("TXN002", "STORE002", "USER001", LocalDateTime.of(2023, 7, 2, 10, 45), products2));

        VirtualList<Transaction> virtualList = new VirtualList<>();
        virtualList.setHeight("400px"); // Set a fixed height for better performance
        virtualList.setItems(transactions);
        virtualList.setRenderer(new ComponentRenderer<>(transaction -> {
            Div transactionIdDiv = new Div();
            Span transactionIdLabel = new Span("Transaction ID: ");
            transactionIdLabel.getStyle().set("font-weight", "bold");
            transactionIdDiv.add(transactionIdLabel);
            transactionIdDiv.add(new Span(transaction.transactionId()));

            Div storeIdDiv = new Div();
            Span storeIdLabel = new Span("Store ID: ");
            storeIdLabel.getStyle().set("font-weight", "bold");
            storeIdDiv.add(storeIdLabel);
            storeIdDiv.add(new Span(transaction.storeId()));

            Div dateOfTransactionDiv = new Div();
            Span dateOfTransactionLabel = new Span("Date of Transaction: ");
            dateOfTransactionLabel.getStyle().set("font-weight", "bold");
            dateOfTransactionDiv.add(dateOfTransactionLabel);
            dateOfTransactionDiv.add(new Span(formatDate(transaction.dateOfTransaction())));

            Button detailsButton = new Button("Details", event -> openDetailsDialog(transaction));

            HorizontalLayout detailsLayout = new HorizontalLayout(detailsButton);
            detailsLayout.setWidthFull();
            detailsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            VerticalLayout itemLayout = new VerticalLayout(transactionIdDiv, storeIdDiv, dateOfTransactionDiv,detailsLayout);
            itemLayout.setPadding(true);
            itemLayout.setWidthFull();

            return itemLayout;
        }));

        content.add(virtualList);
    }

    private void openDetailsDialog(Transaction transaction) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();

        Span transactionId = new Span("Transaction ID: " + transaction.transactionId());
        transactionId.getStyle().set("font-weight", "bold");

        Span storeId = new Span("Store ID: " + transaction.storeId());
        storeId.getStyle().set("font-weight", "bold");

        Span dateOfTransaction = new Span("Date of Transaction: " + formatDate(transaction.dateOfTransaction()));
        dateOfTransaction.getStyle().set("font-weight", "bold");


        layout.add(transactionId, storeId, dateOfTransaction);

        // Add product details
        Span productsLabel = new Span("Products:");
        productsLabel.getStyle().set("font-weight", "bold");
        layout.add(productsLabel);

        for (Map.Entry<Product, Integer> entry : transaction.productToQuantity().entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();

            String productName = product.getProductName();
            if (quantity > 1) {
                productName += "s"; // Add "s" for plural
            }

            Span productDetails = new Span(quantity + " " + productName);
            layout.add(productDetails);
        }

        Button closeButton = new Button("Close", event -> dialog.close());
        closeButton.getStyle().set("margin-top", "20px");

        HorizontalLayout buttonLayout = new HorizontalLayout(closeButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.add(buttonLayout);

        dialog.add(layout);
        dialog.open();
    }

    private String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }

}
