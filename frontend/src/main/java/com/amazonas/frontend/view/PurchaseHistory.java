package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("example5")
public class PurchaseHistory extends BaseLayout {
    private final Grid<Transaction> grid;
    private final AppController appController;
    private final List<Transaction> transactions;

    public PurchaseHistory(AppController appController) {
        super(appController);
        this.appController = appController;

        // Sample transaction data
        transactions = new ArrayList<>();
        transactions.add(new Transaction("1", "User1", "2024-06-29", new ArrayList<>())); // Replace with actual data
        // Add more sample transactions as needed

        // Set the window title
        String newTitle = "Purchase History";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Create and configure the grid
        grid = new Grid<>(Transaction.class);
        grid.setItems(transactions);

        // Clear default columns
        grid.removeAllColumns();

        // Configure the columns manually
        grid.addColumn(Transaction::getId).setHeader("ID");
        grid.addColumn(Transaction::getUserId).setHeader("User ID");
        grid.addColumn(Transaction::getDate).setHeader("Date");
        grid.addColumn(Transaction::getProductsList).setHeader("Products");

        content.add(grid); // Add grid to the content from BaseLayout
    }

    //TODO:
    // make components such as add manager visible to only certain people based on their permissions
    // connect to:
    // GET_STORE_TRANSACTION_HISTORY


    public static class Transaction {
        private String id;
        private String userId;
        private String date;
        private List<String> productsList;

        public Transaction(String id, String userId, String date, List<String> productsList) {
            this.id = id;
            this.userId = userId;
            this.date = date;
            this.productsList = productsList;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<String> getProductsList() {
            return productsList;
        }

        public void setProductsList(List<String> productsList) {
            this.productsList = productsList;
        }
    }
}