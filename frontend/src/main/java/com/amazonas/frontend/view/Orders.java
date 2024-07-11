package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Route("Orders")
public class Orders extends Profile {
    private final AppController appController;
    private List<Transaction> orderList;

    public Orders(AppController appController) {
        super(appController);
        this.appController = appController;
        returnToMainIfNotLogged();
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
        orderGrid.setColumns("transactionId", "storeId", "userId", "dateOfTransaction", "productToQuantity");

        // Add click listener to show order details in a popup
        orderGrid.addItemClickListener(event -> showOrderDetails(event.getItem()));

        ordersLayout.add(orderGrid);
        content.add(ordersLayout);
    }

    private void showOrderDetails(Transaction order) {
        // Create a dialog to show order details
        Dialog dialog = new Dialog();
        dialog.add("Transaction ID: " + order.getTransactionId());
        dialog.add("\nStore ID: " + order.getStoreId());
        dialog.add("\nUser ID: " + order.getUserId());
        dialog.add("\nDate of Transaction: " + order.getDateOfTransaction());
        // add table with product to quantity
        dialog.add("\nProducts: ");
        for (Map.Entry<Product, Integer> entry : order.getProductToQuantity().entrySet()) {
            dialog.add(entry.getKey().getProductName() + " - " + entry.getValue());
        }

        Button closeButton = new Button("Close", _ -> dialog.close());

        dialog.add(closeButton);
        dialog.open();
    }

}
