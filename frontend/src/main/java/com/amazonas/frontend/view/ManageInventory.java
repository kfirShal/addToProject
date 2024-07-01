package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.requests.stores.ProductRequest;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.utils.Converter;
import com.amazonas.frontend.utils.POJOBinder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.function.Function;

@Route("manageinventory")
public class ManageInventory extends BaseLayout implements HasUrlParameter<String> {
    private final Grid<Product> grid;
    private final POJOBinder<Product> binder;
    private final AppController appController;
    private String storeId;
    private final Dialog editDialog;
    private final Dialog addDialog;
    private Product currentProduct;
    private final Map<Boolean, List<Product>> products;

    public ManageInventory(AppController appController) {
        super(appController);
        this.appController = appController;
        binder = new POJOBinder<>(Product.class);
        grid = new Grid<>(Product.class);
        Map<Boolean, List<Product>> fetchedProducts = getProducts();
        products = fetchedProducts == null ? new HashMap<>() : fetchedProducts;

        // Set the window's title
        String newTitle = "Manage Inventory";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        List<Product> allP = new LinkedList<>();
        // Check if products list is null or empty
        if (allP.isEmpty()) {
            addSampleProducts();
        }

        allP.addAll(products.get(true));
        allP.addAll(products.get(false));

        Map<String, Integer> idToQuantity = new HashMap<>();
        allP.forEach(p -> {
            ProductRequest payload = new ProductRequest(storeId, new Product(p.productId()));
            try {
                appController.postByEndpoint(Endpoints.GET_PRODUCT_QUANTITY, payload);
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });

        grid.setItems(allP);

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
            Button editButton = new Button("Edit", click -> openEditDialog(product));
            return editButton;
        });

        grid.addComponentColumn(product -> {
            Button toggleButton = new Button(products.get(true).contains(product) ? "Disable" : "Enable", click -> {
                try {
                    Endpoints endpoint = getProducts().get(true).contains(product) ? Endpoints.DISABLE_PRODUCT : Endpoints.ENABLE_PRODUCT;
                    ProductRequest request = new ProductRequest(storeId, product.productId());
                    appController.postByEndpoint(endpoint, request);
                    refreshGrid();
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
            });
            return toggleButton;
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

        content.add(grid);

        // Create and configure the edit dialog
        editDialog = createProductDialog("Edit Product", this::saveChanges);
        content.add(editDialog);

        // Create and configure the add dialog
        addDialog = createProductDialog("Add Product", this::addProduct);
        content.add(addDialog);

        Button addButton = new Button("Add", click -> openAddDialog());
        HorizontalLayout addButtonLayout = new HorizontalLayout(addButton);
        addButtonLayout.getStyle().set("justify-content", "center");
        content.add(addButtonLayout);

    }

    private Dialog createProductDialog(String dialogTitle, Runnable saveAction) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        // Add title to the dialog
        H2 title = new H2(dialogTitle);
        dialog.add(title);

        FormLayout formLayout = new FormLayout();
        TextField productNameField = new TextField("Product Name");
        binder.bind(productNameField, "productName");
        formLayout.add(productNameField);

        TextField priceField = new TextField("Price");
        binder.bind(priceField, "price").withDoubleConverter();
        formLayout.add(priceField);

        TextField categoryField = new TextField("Category");
        binder.bind(categoryField, "category");
        formLayout.add(categoryField);

        TextField descriptionField = new TextField("Description");
        binder.bind(descriptionField, "description");
        formLayout.add(descriptionField);

        ComboBox<String> ratingField = new ComboBox<>("Rating");
        ratingField.setItems("1", "2", "3", "4", "5");
        binder.bind(ratingField, "rating").withConverter(new Converter<Rating, String>() {
            @Override
            public Class<Rating> fromType() {
                return Rating.class;
            }

            @Override
            public Class<String> toType() {
                return String.class;
            }

            @Override
            public Function<Rating, String> to() {
                return (rating -> String.valueOf(rating.ordinal() + 1));
            }

            @Override
            public Function<String, Rating> from() {
                return ordinal -> Rating.values()[Integer.parseInt(ordinal) - 1];
            }
        });
        formLayout.add(ratingField);

        Button saveButton = new Button("Save Changes", e -> saveAction.run());
        Button discardButton = new Button("Discard", e -> dialog.close());
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, discardButton);

        dialog.add(formLayout, buttonsLayout);

        return dialog;
    }

    private void openEditDialog(Product product) {
        currentProduct = product;
        binder.readObject(product);
        editDialog.open();
    }

    private void saveChanges() {
        binder.writeObject(currentProduct);
        try {
            appController.postByEndpoint(Endpoints.UPDATE_PRODUCT, currentProduct);
            refreshGrid();
            editDialog.close();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private void openAddDialog() {
        currentProduct = new Product("-1");
        binder.readObject(currentProduct);
        addDialog.open();
    }

    private void addProduct() {
        binder.writeObject(currentProduct);
        try {
            appController.postByEndpoint(Endpoints.ADD_PRODUCT, currentProduct);
            refreshGrid();
            addDialog.close();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private Map<Boolean, List<Product>> getProducts() {
        Map<Boolean, List<Product>> map = null;
        try {
            List<Map<Boolean, List<Product>>> fetched = appController.postByEndpoint(Endpoints.GET_STORE_PRODUCTS, storeId);
            map = fetched.getFirst();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
        return map;
    }

    private Map<Boolean, List<Product>> addSampleProducts() {
        products.computeIfAbsent(true, _ -> new LinkedList<>());
        products.computeIfAbsent(false, _ -> new LinkedList<>());

        products.get(true).add(new Product("1", "Product 1", 100.0, "Category 1", "Description 1", Rating.FIVE_STARS));
        products.get(false).add(new Product("2", "Product 2", 150.0, "Category 2", "Description 2", Rating.FOUR_STARS));
        return products;
    }

    private void refreshGrid() {
        Map<Boolean, List<Product>> products = getProducts();
        if (products == null || products.isEmpty()) {
            products = addSampleProducts();
        }
        List<Product> allP = new LinkedList<>();
        allP.addAll(products.get(true));
        allP.addAll(products.get(false));
        grid.setItems(allP);
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String s) {
        storeId = s;
    }
}
