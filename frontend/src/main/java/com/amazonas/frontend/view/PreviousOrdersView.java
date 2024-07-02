package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.common.requests.Request;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.common.utils.Rating;

import java.time.LocalDateTime;
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
            Request request = new Request(AppController.getCurrentUserId(),"","");
            transactions = appController.postByEndpoint(Endpoints.GET_USER_TRANSACTION_HISTORY, request);
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
        transactions.add(new Transaction("TXN001", "STORE001", "USER001", LocalDateTime.of(2023, 7, 1, 15, 30), products1, TransactionState.COMPLETED));

        // Transaction 2
        Map<Product, Integer> products2 = new HashMap<>();
        products2.put(new Product("P003", "Smartphone", 800.00, "Electronics", "A latest model smartphone.", Rating.FIVE_STAR, "STORE002"), 1);
        products2.put(new Product("P004", "Headphones", 50.00, "Accessories", "Noise-cancelling headphones.", Rating.FIVE_STAR, "STORE002"), 1);
        transactions.add(new Transaction("TXN002", "STORE002", "USER001", LocalDateTime.of(2023, 7, 2, 10, 45), products2, TransactionState.PENDING));


    }
}
