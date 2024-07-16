package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.amazonas.frontend.control.AppController.isUserLoggedIn;

@Route("Orders")
public class Orders extends Profile implements BeforeEnterObserver {
    private final AppController appController;
    private List<Transaction> orderList;

    public Orders(AppController appController) {
        super(appController);
        this.appController = appController;
        if (!isUserLoggedIn()) {
            UI.getCurrent().navigate("");
            return;
        }
        InitialOrders();
        createOrdersLayout();
    }

    private void InitialOrders() {
        orderList = new ArrayList<>();
        // get user transaction history
        String userID = AppController.getCurrentUserId();
        try {
            orderList = appController.postByEndpoint(Endpoints.GET_USER_TRANSACTION_HISTORY, userID);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    private void createOrdersLayout() {
        VerticalLayout ordersLayout = new VerticalLayout();

        // Grid to display orders
        Grid<Transaction> orderGrid = new Grid<>(Transaction.class);
        orderGrid.setItems(orderList);
        orderGrid.setColumns("transactionId", "storeId", "userId", "dateOfTransaction", "productToQuantity", "state");

        // Add click listener to navigate to the order URL
        orderGrid.addItemClickListener(event -> UI.getCurrent().navigate("orders?orderId=" + event.getItem().getTransactionId()));

        ordersLayout.add(orderGrid);
        content.add(ordersLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        List<String> orderIds = queryParameters.getParameters().getOrDefault("orderId", List.of());

        if (!orderIds.isEmpty()) {
            String orderId = orderIds.getFirst();
            Transaction order = findOrderById(orderId);
            if (order != null) {
                showOrderDetails(order);
            }
        }
    }

    private Transaction findOrderById(String orderId) {
        return orderList.stream()
                .filter(order -> order.getTransactionId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    private void showOrderDetails(Transaction order) {
        // Create a dialog to show order details
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();

        // Add components to the layout for each piece of information
        layout.add(new Span("Order Details:"));
        layout.add(new Span("Transaction ID: " + order.getTransactionId()));
        layout.add(new Span("Store ID: " + order.getStoreId()));
        layout.add(new Span("User ID: " + order.getUserId()));
        layout.add(new Span("Date of Transaction: " + order.getDateOfTransaction()));

        // Add a component for products
        VerticalLayout productsLayout = new VerticalLayout();
        productsLayout.add(new Span("Products: "));
        for (Map.Entry<Product, Integer> entry : order.getProductToQuantity().entrySet()) {
            productsLayout.add(new Span(entry.getKey().getProductName() + " - " + entry.getValue()));
        }
        layout.add(productsLayout);

        layout.add(new Span("State: " + order.getState()));

        // Add the layout to the dialog
        dialog.add(layout);

        Button closeButton = new Button("Close", _ -> dialog.close());

        dialog.add(closeButton);
        dialog.open();
    }
}
