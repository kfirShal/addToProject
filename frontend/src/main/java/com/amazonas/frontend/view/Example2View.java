package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.ArrayList;
import java.util.List;

@Route("example2")
public class Example2View extends BaseLayout {

    private final AppController appController;
    private final Grid<Product> grid;
    private final List<Product> products;
    private final Binder<Product> binder;
    private final Editor<Product> editor;

    public Example2View(AppController appController) {
        super(appController);
        this.appController = appController;

        // Sample product data
        products = new ArrayList<>();
        products.add(new Product("1", "Product 1", "Category 1", 4.5, 100.0, "Description 1", 10));
        products.add(new Product("2", "Product 2", "Category 2", 3.8, 150.0, "Description 2", 20));
        // Add more sample products as needed

        // Set the window title
        String newTitle = "Manage Inventory";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Create and configure the grid
        grid = new Grid<>(Product.class);
        grid.setItems(products);

        // Clear default columns
        grid.removeAllColumns();

        // Configure the columns manually
        grid.addColumn(Product::getId).setHeader("ID");
        grid.addColumn(Product::getName).setHeader("Name").setEditorComponent(new TextField());
        grid.addColumn(Product::getCategory).setHeader("Category").setEditorComponent(new TextField());
        grid.addColumn(Product::getRating).setHeader("Rating").setEditorComponent(new TextField());
        grid.addColumn(Product::getPrice).setHeader("Price").setEditorComponent(new TextField());
        grid.addColumn(Product::getDescription).setHeader("Description").setEditorComponent(new TextField());
        grid.addColumn(Product::getQuantity).setHeader("Quantity").setEditorComponent(new TextField());

        // Add a column for the remove button
        grid.addComponentColumn(product -> {
            Button editButton = new Button("Edit", click -> {
                grid.getEditor().editItem(product);
            });
            return editButton;
        }).setHeader("Edit");

        grid.addComponentColumn(product -> {
            Button removeButton = new Button("Remove", click -> {
                products.remove(product);
                grid.setItems(products);
            });
            return removeButton;
        }).setHeader("Actions");

        // Configure the editor
        binder = new Binder<>(Product.class);
        editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        content.add(grid); // Add grid to the content from BaseLayout

        // Add buttons to save, cancel, and add a new product
        Button saveButton = new Button("Save", click -> editor.save());
        Button cancelButton = new Button("Cancel", click -> editor.cancel());
        Button addButton = new Button("Add", click -> addNewProduct());

        VerticalLayout buttonsLayout = new VerticalLayout(saveButton, cancelButton, addButton);
        content.add(buttonsLayout); // Add buttons to the content from BaseLayout
    }

    private void addNewProduct() {
        Product newProduct = new Product("", "", "", 0.0, 0.0, "", 0);
        products.add(newProduct);
        grid.setItems(products);
        grid.getEditor().editItem(newProduct);
    }

    public static class Product {
        private String id;
        private String name;
        private String category;
        private double rating;
        private double price;
        private String description;
        private int quantity;

        public Product(String id, String name, String category, double rating, double price, String description, int quantity) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.rating = rating;
            this.price = price;
            this.description = description;
            this.quantity = quantity;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
