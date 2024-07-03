package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route("Orders")
public class Orders extends Profile {
    private AppController appController;
    private List<Order> orderList;
    private Grid<Order> orderGrid;

    public Orders(AppController appController) {
        super(appController);
        this.appController = appController;
        returnToMainIfNotLogged();
        initializeOrderList();
        createOrdersLayout();
    }

    private void initializeOrderList() {
        orderList = new ArrayList<>();
        // Example orders
        orderList.add(new Order(1, "Order001", LocalDate.now(), "Completed"));
        orderList.add(new Order(2, "Order002", LocalDate.now().minusDays(1), "Shipped"));
    }

    private void createOrdersLayout() {
        VerticalLayout ordersLayout = new VerticalLayout();

        // Grid to display orders
        orderGrid = new Grid<>(Order.class);
        orderGrid.setItems(orderList);
        orderGrid.setColumns("id", "orderNumber", "date", "status");

        // Add button to add new order for demonstration
        Button addOrderButton = new Button("Add Order", event -> {
            addOrder();
        });

        // Add click listener to show order details in a popup
        orderGrid.addItemClickListener(event -> {
            showOrderDetails(event.getItem());
        });

        ordersLayout.add(orderGrid, addOrderButton);
        content.add(ordersLayout);
    }

    private void addOrder() {
        // Example method to add a new order
        int newOrderId = orderList.size() + 1;
        Order newOrder = new Order(newOrderId, "Order00" + newOrderId, LocalDate.now(), "Pending");
        orderList.add(newOrder);
        orderGrid.getDataProvider().refreshAll();
        Notification.show("New order added");
    }

    private void showOrderDetails(Order order) {
        // Create a dialog to show order details
        Dialog dialog = new Dialog();
        dialog.add("Order ID: " + order.getId());
        dialog.add("\nOrder Number: " + order.getOrderNumber());
        dialog.add("\nDate: " + order.getDate());
        dialog.add("\nStatus: " + order.getStatus());

        Button closeButton = new Button("Close", event -> {
            dialog.close();
        });

        dialog.add(closeButton);
        dialog.open();
    }

    // Inner class representing an Order
    public static class Order {
        private int id;
        private String orderNumber;
        private LocalDate date;
        private String status;

        public Order(int id, String orderNumber, LocalDate date, String status) {
            this.id = id;
            this.orderNumber = orderNumber;
            this.date = date;
            this.status = status;
        }

        // Getters and setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(String orderNumber) {
            this.orderNumber = orderNumber;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
