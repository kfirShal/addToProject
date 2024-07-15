package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.*;

@Route("purchasehistory")
public class PurchaseHistory extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;
    private String storeId;
    private Grid<Transaction> grid;
    private List<Transaction> transactions;;

    public PurchaseHistory(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    private void createView(){
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
        grid.addColumn(transaction -> collectionToString(transaction.getProductToQuantity().keySet())).setHeader("Products");

        storeId = getParam("storeid");
        fetchAndPopulateTransactions();
        content.add(grid);
    }

    private String collectionToString(Collection<Product> products) {
        StringBuilder builder = new StringBuilder();
        for(Product p: products){
            builder.append(p.getProductName()).append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }

    private void fetchAndPopulateTransactions() {
        try {
            transactions = appController.postByEndpoint(Endpoints.GET_STORE_TRANSACTION_HISTORY, storeId);
            grid.setItems(transactions);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }
}
