package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("inventory")
public class InventoryManagementView extends BaseLayout {

    private final Grid<Product> grid;
    private final Binder<Product> binder;
    private final AppController appController;
    String storeId = "get it from somewhere";

    public InventoryManagementView(AppController appController) {
        super(appController);
        this.appController = appController;

        grid = new Grid<>(Product.class);
        grid.setItems(getProducts());

        // Configure the columns
        grid.addColumn(Product::productId).setHeader("ID");
        grid.addColumn(Product::productName).setHeader("Name");
        grid.addColumn(Product::category).setHeader("Category");
        grid.addColumn(Product::rating).setHeader("Rating");
        grid.addColumn(Product::price).setHeader("Price");
        grid.addColumn(Product::description).setHeader("Description");

        // Add action buttons
        grid.addComponentColumn(product -> {
            Button addButton = new Button("Add Product", click -> {
                try {
                    appController.postByEndpoint(Endpoints.ADD_PRODUCT, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
                refreshGrid();
            });
            return addButton;
        }).setHeader("Add Product");

        grid.addComponentColumn(product -> {
            Button editButton = new Button("Edit", click -> {
                try {
                    appController.postByEndpoint(Endpoints.UPDATE_PRODUCT, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
                refreshGrid();
            });
            return editButton;
        }).setHeader("Edit");


        grid.addComponentColumn(product -> {
            Button removeButton = new Button("Remove", click -> {
                try {
                    appController.postByEndpoint(Endpoints.REMOVE_PRODUCT, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
                refreshGrid();
            });
            return removeButton;
        }).setHeader("Remove");

//        grid.addComponentColumn(product -> {
//            Button toggleButton = new Button(product.isEnabled() ? "Disable" : "Enable", click -> {
//                if (product.isEnabled()) {
//                    restTemplate.postForObject(Endpoints.DISABLE_PRODUCT.location(), product.productId(), Void.class);
//                    product.setEnabled(false);
//                } else {
//                    restTemplate.postForObject(Endpoints.ENABLE_PRODUCT.location(), product.productId(), Void.class);
//                    product.setEnabled(true);
//                }
//                refreshGrid();
//            });
//            return toggleButton;
//        }).setHeader("Disable/Enable");

        // Configure the editor
        binder = new Binder<>(Product.class);
        Editor<Product> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        // Add editor fields
        TextField nameField = new TextField();
        binder.forField(nameField).bind(Product::productName, Product::setProductName);
        grid.getColumnByKey("productName").setEditorComponent(nameField);

        TextField categoryField = new TextField();
        binder.forField(categoryField).bind(Product::category, Product::setCategory);
        grid.getColumnByKey("category").setEditorComponent(categoryField);

//        TextField ratingField = new TextField();
//        binder.forField(ratingField).bind(Product::rating, Product::setRating);
//        grid.getColumnByKey("rating").setEditorComponent(ratingField);
//
//        TextField priceField = new TextField();
//        binder.forField(priceField).bind(Product::price, Product::setPrice);
//        grid.getColumnByKey("price").setEditorComponent(priceField);

        TextField descriptionField = new TextField();
        binder.forField(descriptionField).bind(Product::description, Product::setDescription);
        grid.getColumnByKey("description").setEditorComponent(descriptionField);

        // Add save and cancel buttons for the editor
        Button saveButton = new Button("Save", click -> {
            try {
                editor.save();
                appController.postByEndpoint(Endpoints.UPDATE_PRODUCT, storeId);
                refreshGrid();
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });
        Button cancelButton = new Button("Cancel", click -> editor.cancel());

        content.add(grid, saveButton, cancelButton);
        addToDrawer(content);
    }

    private List<Product> getProducts() {
        List<Product> products = null;
        try {
            products = appController.postByEndpoint(Endpoints.GET_STORE_PRODUCTS, storeId);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
        return products;
    }

    private void refreshGrid() {
        grid.setItems(getProducts());
    }
}
