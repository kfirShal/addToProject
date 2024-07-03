package com.amazonas.frontend.view;

import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.amazonas.common.dtos.Product;

import java.util.*;
import java.util.List;


@Route("Cart")
public class Cart extends Profile {
    protected List<Item> items;
    protected Grid<Item> grid;


    public Cart(AppController appController) {
        super(appController);

        // check if user logged in, if not return to home page
        returnToMainIfNotLogged();

        items = new ArrayList<>();
        grid = new Grid<>(Item.class, false);
        configureGrid();

        Button addButton = new Button("Generate Random Item", _ -> generateRandomItem());
        // add in the bottom right button for proceed to checkout
        Button checkoutButton = new Button("Checkout", _ -> UI.getCurrent().navigate("Payment"));
        checkoutButton.setIcon(VaadinIcon.CHECK.create());
        checkoutButton.getStyle().set("margin-left", "auto");
        content.add(addButton, grid, checkoutButton);
    }

    private void configureGrid() {
        grid.addColumn(Item::getName).setHeader("Name");
        grid.addColumn(Item::getPrice).setHeader("Price");
        grid.addColumn(Item::getQuantity).setHeader("Quantity");
        grid.addColumn(item -> item.getPrice() * item.getQuantity()).setHeader("Total Price");
        grid.getColumns().get(1).setWidth("10px");
        grid.getColumns().get(2).setWidth("10px");

        grid.addColumn(new ComponentRenderer<>(item -> {
            HorizontalLayout layout = new HorizontalLayout();
            Button addButton = new Button("+", _ -> {
                item.setQuantity(item.getQuantity() + 1);

                grid.getDataProvider().refreshItem(item);
            });
            Button removeButton = new Button("-", _ -> {
                item.setQuantity(item.getQuantity() > 1 ? item.getQuantity() - 1 : 1);
                grid.getDataProvider().refreshItem(item);
            });
            Button deleteButton = new Button("Remove", _ -> {
                items.remove(item);
                grid.setItems(items);
            });

            layout.add(addButton, removeButton, deleteButton);
            return layout;
        })).setHeader("Actions");

        grid.addItemClickListener(event -> {
            // when click on item, open pop up with full deatils
            Dialog dialog = new Dialog();
            dialog.add(new Paragraph("Product ID: " + event.getItem().getProductId()));
            dialog.add(new Paragraph("Category: " + event.getItem().getCategory()));
            dialog.add(new Paragraph("Description: " + event.getItem().getDescription()));
            dialog.add(new Paragraph("Rating: " + event.getItem().getRating()));
            dialog.open();

        });

        grid.setItems(items);
    }

    private void generateRandomItem() {
        Random random = new Random();
        String[] itemNames = {"Banana", "Apple", "Ceral", "Milk", "Bread", "Eggs", "Butter"};
        int price = random.nextInt(3,28);
        String name = itemNames[random.nextInt(itemNames.length)];
        String id = UUID.randomUUID().toString();

        Product product = new Product(id, name, price, "Grocery", "This is a random item", Rating.FIVE_STARS);
        Item newItem = new Item(product);
        items.add(newItem);
        grid.setItems(items);
    }


    protected static class Item {
        private final String productId;
        private final String productName;
        private final double price;
        private final String category;
        private final String description;
        private final Rating rating;
        private int quantity;


        public Item(Product product) {
            this.productId = product.productId();
            this.productName = product.productName();
            this.price = product.price();
            this.category = product.category();
            this.description = product.description();
            this.rating = product.rating();
            this.quantity = 1;
        }

        public String getName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getProductId() {
            return productId;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public Rating getRating() {
            return rating;
        }
    }
}
