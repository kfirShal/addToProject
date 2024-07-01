package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.requests.stores.ProductRequest;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.utils.POJOBinder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route("example2")
public class Example2View extends BaseLayout {
    private final Grid<Product> grid;
    private final POJOBinder<Product> binder;
    private final AppController appController;
    private final String storeId = "get it from somewhere";
    private Registration editorOpenListener;
    private Registration editorCloseListener;
    private HorizontalLayout buttonsLayout;

    public Example2View(AppController appController) {
        super(appController);
        this.appController = appController;
        binder = new POJOBinder<>(Product.class);
        grid = new Grid<>(Product.class);
        List<Product> products = getProducts();

        // Set the window's title
        String newTitle = "Manage Inventory";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Check if products list is null or empty
        if (products == null || products.isEmpty()) {
            products = getSampleProducts();
        }

        Map<String, Integer> idToQuantity = new HashMap<>();
        products.forEach(p -> {
            ProductRequest payload = new ProductRequest(storeId, new Product(p.productId()));
            try {
                appController.postByEndpoint(Endpoints.GET_PRODUCT_QUANTITY, payload);
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });

        grid.setItems(products);

        // Configure the columns
        grid.addColumn(Product::productId).setHeader("ID");
        grid.addColumn(Product::productName).setHeader("Name");
        grid.addColumn(Product::price).setHeader("Price");
        grid.addColumn(Product::category).setHeader("Category");
        grid.addColumn(Product::description).setHeader("Description");
        grid.addColumn(Product::rating).setHeader("Rating");
        grid.addColumn(p -> idToQuantity.get(p.productId())).setHeader("Quantity");

        // Add action buttons
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
        });

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
        });

//        grid.addComponentColumn(product -> {
//            Button enableDisableButton = new Button(product.isEnabled() ? "Disable" : "Enable", click -> {
//                product.setEnabled(!product.isEnabled());
//                try {
//                    appController.postByEndpoint(Endpoints.UPDATE_PRODUCT, storeId); // Assuming this endpoint also updates the enabled state
//                } catch (ApplicationException e) {
//                    openErrorDialog(e.getMessage());
//                }
//                refreshGrid();
//            });
//            return enableDisableButton;
//        });

        // Configure the editor
        Editor<Product> editor = grid.getEditor();
        editor.setBuffered(true);

        // Add save and cancel buttons for the editor
        Button addButton = new Button("Add", click -> {
            try {
                appController.postByEndpoint(Endpoints.ADD_PRODUCT, storeId);
                refreshGrid();
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });

        Button saveButton = new Button("Save Edit", click -> {
            try {
                editor.save();
                appController.postByEndpoint(Endpoints.UPDATE_PRODUCT, storeId);
                refreshGrid();
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });
        Button cancelButton = new Button("Cancel Edit", click -> editor.cancel());

        buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        content.add(grid, buttonsLayout, addButton);

        // Configure editor open and close listeners
        editorOpenListener = editor.addOpenListener(event -> {
            buttonsLayout.setVisible(true);
        });

        editorCloseListener = editor.addCloseListener(event -> {
            buttonsLayout.setVisible(false);
        });
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

    private List<Product> getSampleProducts() {
        List<Product> sampleProducts = new ArrayList<>();
        sampleProducts.add(new Product("1", "Product 1", 100.0, "Category 1", "Description 1", Rating.FIVE_STARS));
        sampleProducts.add(new Product("2", "Product 2", 150.0, "Category 2", "Description 2", Rating.FOUR_STARS));
        return sampleProducts;
    }

    private void refreshGrid() {
        List<Product> products = getProducts();
        if (products == null || products.isEmpty()) {
            products = getSampleProducts();
        }
        grid.setItems(products);
    }
}
