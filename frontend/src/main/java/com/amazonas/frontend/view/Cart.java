package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.amazonas.frontend.control.AppController.isUserLoggedIn;

@Route("Cart")
public class Cart extends Profile {
    private List<RandomItem> items;
    private Grid<RandomItem> grid;

    public Cart(AppController appController) {
        super(appController);

        // check if user logged in, if not return to home page
        returnToMainIfNotLogged();

        items = new ArrayList<>();
        grid = new Grid<>(RandomItem.class, false);
        configureGrid();

        Button addButton = new Button("Generate Random Item", event -> generateRandomItem());
        // add in the bottom right button for proceed to checkout
        Button checkoutButton = new Button("Checkout", event -> {
            UI.getCurrent().navigate("Checkout");
        });
        checkoutButton.setIcon(VaadinIcon.CHECK.create());
        checkoutButton.getStyle().set("margin-left", "auto");
        content.add(addButton, grid, checkoutButton);
    }

    private void configureGrid() {
        grid.addColumn(RandomItem::getName).setHeader("Name");
        grid.addColumn(RandomItem::getPrice).setHeader("Price");
        grid.addColumn(RandomItem::getQuantity).setHeader("Quantity");
        grid.addColumn(item -> item.getPrice() * item.getQuantity()).setHeader("Total Price");
        grid.getColumns().get(1).setWidth("10px");
        grid.getColumns().get(2).setWidth("10px");

        grid.addColumn(new ComponentRenderer<>(item -> {
            HorizontalLayout layout = new HorizontalLayout();
            Button addButton = new Button("+", click -> {
                item.setQuantity(item.getQuantity() + 1);

                grid.getDataProvider().refreshItem(item);
            });
            Button removeButton = new Button("-", click -> {
                item.setQuantity(item.getQuantity() > 1 ? item.getQuantity() - 1 : 1);
                grid.getDataProvider().refreshItem(item);
            });
            Button deleteButton = new Button("Remove", click -> {
                items.remove(item);
                grid.setItems(items);
            });
            layout.add(addButton, removeButton, deleteButton);
            return layout;
        })).setHeader("Actions");

        grid.addItemClickListener(event -> {
            RandomItem item = event.getItem();
            UI.getCurrent().navigate("Item/" + item.getName());
        });

        grid.setItems(items);
    }

    private void generateRandomItem() {
        Random random = new Random();
        String[] itemNames = {"Banana", "Apple", "Ceral", "Milk", "Bread", "Eggs", "Butter"};
        int price = random.nextInt(3,28);
        String name = itemNames[random.nextInt(itemNames.length)];
        String id = UUID.randomUUID().toString();

        RandomItem newItem = new RandomItem(id, name, price, 1);
        items.add(newItem);
        grid.setItems(items);
    }


    // This class is used to generate random items for the cart
    protected static class RandomItem {
        private String id;
        private String name;
        private int price;
        private int quantity;


        public RandomItem(String id, String name, int price, int quantity) {
                this.id = id;
                this.name = name;
                this.price = price;
                this.quantity = quantity;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public int getPrice() {
                return price;
            }

            public int getQuantity() {
                return quantity;
            }

            public void setQuantity(int quantity) {
                this.quantity = quantity;
            }
        }
}
