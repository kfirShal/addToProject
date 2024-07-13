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

@Route("purchasehistory")
public class PurchaseHistory extends BaseLayout {
    private final AppController appController;
    private String storeId;
    private final Grid<Transaction> grid;
    private final List<Transaction> transactions;

    public PurchaseHistory(AppController appController) {
        super(appController);
        this.appController = appController;
        storeId = getParam("storeid");

        // Set the window title
        String newTitle = "Purchase History";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        grid = new Grid<>(Transaction.class);

        grid.removeAllColumns();

        grid.addColumn(Transaction::getTransactionId).setHeader("ID");
        grid.addColumn(Transaction::getUserId).setHeader("User ID");
        grid.addColumn(Transaction::getDateOfTransaction).setHeader("Date");
        grid.addColumn(transaction -> transaction.getProductToQuantity().keySet().toString()).setHeader("Products");

        content.add(grid);

        transactions = new ArrayList<>();

        fetchAndPopulateTransactions();

        grid.setItems(transactions);
    }

    private void fetchAndPopulateTransactions() {
        try {
            List<Transaction> fetchedTransactions = appController.postByEndpoint(Endpoints.GET_STORE_TRANSACTION_HISTORY, storeId);

            transactions.clear();
            transactions.addAll(fetchedTransactions);

            grid.setItems(transactions);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }
}
