package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Transaction;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("example5")
public class PurchaseHistory extends BaseLayout {
    private final AppController appController;
    private final Grid<Transaction> grid;
    private final List<Transaction> transactions;
    private String storeId;

    public PurchaseHistory(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window title
        String newTitle = "Purchase History";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Create and configure the grid
        grid = new Grid<>(Transaction.class);

        // Clear default columns
        grid.removeAllColumns();

        // Configure the columns manually
        grid.addColumn(Transaction::transactionId).setHeader("ID");
        grid.addColumn(Transaction::userId).setHeader("User ID");
        grid.addColumn(Transaction::dateOfTransaction).setHeader("Date");
        grid.addColumn(transaction -> transaction.productToQuantity().keySet().toString()).setHeader("Products"); // Assuming getProductToQuantity() returns a Map<String, Integer>

        content.add(grid); // Add grid to the content from BaseLayout

        // Initialize transactions list
        transactions = new ArrayList<>();

        // Fetch and populate transactions from backend
        fetchAndPopulateTransactions();

        // Set items in the grid
        grid.setItems(transactions);
    }

    private void fetchAndPopulateTransactions() {
        try {
            // Call the endpoint to fetch transactions
            List<Transaction> fetchedTransactions = appController.postByEndpoint(Endpoints.GET_STORE_TRANSACTION_HISTORY, storeId);

            // Update the local transactions list
            transactions.clear();
            transactions.addAll(fetchedTransactions);

            // Refresh grid with updated data
            grid.setItems(transactions);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }
}
