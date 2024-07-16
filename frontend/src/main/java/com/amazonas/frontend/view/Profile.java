package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.amazonas.frontend.control.AppController.getCurrentUserId;

@Route("Profile")
public class Profile extends BaseLayout {

    public Profile(AppController appController) {
        super(appController);
        // check if user logged in, if not return to home page
        returnToMainIfNotLogged();


        // Header
        String message = "My Profile";
        H2 header = new H2(message);
        header.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        content.add(header);

        // Sidebar
        this.nav1.removeAll();
        nav1.addItem(new SideNavItem("Profile", Profile.class, VaadinIcon.USER.create()));
        nav1.addItem(new SideNavItem("Cart", Cart.class, VaadinIcon.CART.create()));
        nav1.addItem(new SideNavItem("Orders", Orders.class, VaadinIcon.CART.create()));

        this.nav2.removeAll();
        nav2.addItem(new SideNavItem("Home", WelcomeView.class, VaadinIcon.HOME.create()));
        nav2.addItem(new SideNavItem("Settings", Settings.class, VaadinIcon.COG.create()));
        nav2.addItem(new SideNavItem("Help", Help.class, VaadinIcon.QUESTION.create()));

        // Only if the class is Profile and not one of the inherts continue
        if (!this.getClass().getName().equals("com.amazonas.frontend.view.Profile")) {
            return;
        }

        // Profile summary layout
        VerticalLayout profileSummaryLayout = new VerticalLayout();
        profileSummaryLayout.setWidthFull();
        profileSummaryLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // User name
        String userName = getCurrentUserId();
        H3 userNameHeader = new H3("Welcome, " + userName);
        profileSummaryLayout.add(userNameHeader);

        // Recent orders
        List<Order> recentOrders = getRecentOrders(); // Assuming a method to get recent orders
        if (!recentOrders.isEmpty()) {
            H3 recentOrdersHeader = new H3("Recent Orders");
            profileSummaryLayout.add(recentOrdersHeader);

            Grid<Order> orderGrid = new Grid<>(Order.class);
            orderGrid.setItems(recentOrders);
            orderGrid.setColumns("orderNumber", "date", "status");
            orderGrid.addItemClickListener(event -> showOrderDetails(event.getItem()));
            profileSummaryLayout.add(orderGrid);
        } else {
            profileSummaryLayout.add(new Paragraph("You have no recent orders."));
        }

        // Personalized offers
        H3 offersHeader = new H3("Personalized Offers");
        profileSummaryLayout.add(offersHeader);
        List<String> offers = getPersonalizedOffers(); // Assuming a method to get personalized offers
        for (String offer : offers) {
            profileSummaryLayout.add(new Paragraph(offer));
        }

        // Add more sections as needed
        // Example: Loyalty points, account settings summary, etc.

        // Add profile summary layout to the content
        content.add(profileSummaryLayout);
    }

    private List<Order> getRecentOrders() {
        // Example implementation to fetch recent orders
        List<Order> recentOrders = new ArrayList<>();
        recentOrders.add(new Order(1, "Order001", LocalDate.now().minusDays(1), "Completed"));
        recentOrders.add(new Order(2, "Order002", LocalDate.now().minusDays(5), "Shipped"));
        return recentOrders;
    }

    private List<String> getPersonalizedOffers() {
        // Example implementation to fetch personalized offers
        List<String> offers = new ArrayList<>();
        offers.add("10% off on your next purchase!");
        offers.add("Free shipping on orders over $50.");
        return offers;
    }

    private void showOrderDetails(Order order) {
        // Create a dialog to show order details
        Dialog dialog = new Dialog();
        dialog.add("Order ID: " + order.getId());
        dialog.add("\nOrder Number: " + order.getOrderNumber());
        dialog.add("\nDate: " + order.getDate());
        dialog.add("\nStatus: " + order.getStatus());

        Button closeButton = new Button("Close", _ -> {
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
