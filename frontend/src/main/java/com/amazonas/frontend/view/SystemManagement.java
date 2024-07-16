package com.amazonas.frontend.view;

import com.amazonas.common.requests.stores.StoreStaffRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("systemmanagement")
public class SystemManagement extends BaseLayout {
    private final AppController appController;
    private String userId;
    private String serviceId;

    public SystemManagement(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window title
        String newTitle = "System Management";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);

        Button shutdownButton = new Button("Shutdown System", click -> {
            try {
                appController.postByEndpoint(Endpoints.SHUTDOWN_MARKET, null);
                showNotification("System shutdown initiated.");
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });

        Button startButton = new Button("Start System", click -> {
            try {
                appController.postByEndpoint(Endpoints.START_MARKET, null);
                showNotification("System started successfully.");
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });

        shutdownButton.getStyle().set("background-color", "red");
        shutdownButton.getStyle().set("color", "white");
        startButton.getStyle().set("background-color", "green");
        startButton.getStyle().set("color", "white");
        HorizontalLayout buttonsLayout = new HorizontalLayout(shutdownButton, startButton);
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.add(buttonsLayout);











        // Shipping Companies Grid
        H3 shippingTitle = new H3("Shipping Companies");
        shippingTitle.getStyle().set("margin-top", "30px");
        Grid<String> shippingGrid = new Grid<>();
        shippingGrid.addColumn(name -> name).setHeader("Company Name");

        shippingGrid.addComponentColumn(name -> {
            Button removeButton = new Button("Remove", click -> {
            });
            return removeButton;
        });

        Button addShippingButton = new Button("Add Company", click -> {
        });

        VerticalLayout shippingLayout = new VerticalLayout(shippingTitle, shippingGrid, addShippingButton);
        content.add(shippingLayout);
        
        // Payment Companies Grid
        H3 paymentTitle = new H3("Payment Companies");
        paymentTitle.getStyle().set("margin-top", "30px");
        Grid<String> paymentGrid = new Grid<>();
        paymentGrid.addColumn(name -> name).setHeader("Company Name");

        paymentGrid.addComponentColumn(name -> {
            Button removeButton = new Button("Remove", click -> {
//                try {
//                    PaymentServiceManagementRequest request = new PaymentS.erviceManagementRequest(serviceId, );
//                    appController.postByEndpoint(Endpoints.REMOVE_PAYMENT_SERVICE, request);
//                    refreshGrid();
//                } catch (ApplicationException e) {
//                    openErrorDialog(e.getMessage());
//                }
            });
            return removeButton;
        });

        Button addPaymentButton = new Button("Add Company", click -> {
        });

        VerticalLayout paymentLayout = new VerticalLayout(paymentTitle, paymentGrid, addPaymentButton);
        content.add(paymentLayout);
    }
}

//TODO: PaymentServiceManagementRequest IS NOT IN common
