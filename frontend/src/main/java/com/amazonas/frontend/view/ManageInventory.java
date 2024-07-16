package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.requests.stores.ProductRequest;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.utils.Converter;
import com.amazonas.frontend.utils.POJOBinder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.function.Function;

@Route("manageinventory")
public class ManageInventory extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;
    private String storeId;
    private Grid<Product> grid;
    private POJOBinder<Product> binder;
    private Map<Boolean, List<Product>> products;
    private Dialog editDialog;
    private Dialog addDialog;
    private Product currentProduct;

    public ManageInventory(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    private void createView(){
        storeId = getParam("storeid");
        binder = new POJOBinder<>(Product.class);
        grid = new Grid<>(Product.class);
        Map<Boolean, List<Product>> fetchedProducts = getProducts();
        products = fetchedProducts == null ? new HashMap<>() : fetchedProducts;

        grid.removeAllColumns();

        // Set the window's title
        String newTitle = "Manage Inventory";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);

        List<Product> allP = new LinkedList<>();
        if (allP.isEmpty()) {
        }
        allP.addAll(products.get(true));
        allP.addAll(products.get(false));

        Map<String, Integer> idToQuantity = new HashMap<>();
        allP.forEach(p -> {
            ProductRequest request = new ProductRequest(storeId, new Product(p.getProductId()));
            try {
                List<Integer> quantity = appController.postByEndpoint(Endpoints.GET_PRODUCT_QUANTITY, request);
                if (quantity.get(0) != null) {
                    idToQuantity.put(p.getProductId(), quantity.get(0));
                }
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });

        grid.setItems(allP);

        // Configure the columns
        grid.addColumn(Product::getProductId).setHeader("ID");
        grid.addColumn(Product::getProductName).setHeader("Name");
        grid.addColumn(Product::getPrice).setHeader("Price");
        grid.addColumn(Product::getCategory).setHeader("Category");
        grid.addColumn(Product::getDescription).setHeader("Description");
        grid.addColumn(Product::getRating).setHeader("Rating");

        grid.addComponentColumn(product -> {
            MultiSelectComboBox<String> keywordsComboBox = new MultiSelectComboBox<>();
            keywordsComboBox.setItems(product.getKeyWords());
            keywordsComboBox.setValue(new HashSet<>(product.getKeyWords()));
            return keywordsComboBox;
        }).setHeader("Keywords");

        grid.addComponentColumn(product -> {
            VerticalLayout layout = new VerticalLayout();
            TextField quantityField = new TextField("");
            Integer quantity = idToQuantity.get(product.getProductId());
            if (quantity != null) {
                quantityField.setValue(quantity.toString());
            }
            quantityField.addValueChangeListener(event -> {
                if (permissionsProfile.hasPermission(storeId, StoreActions.SET_PRODUCT_QUANTITY)) {
                    String value = event.getValue();
                    try {
                        int newQuantity = Integer.parseInt(value);
                        idToQuantity.put(product.getProductId(), newQuantity);
                        ProductRequest request = new ProductRequest(storeId, product.getProductId());
                        appController.postByEndpoint(Endpoints.SET_PRODUCT_QUANTITY, request);
                    } catch (NumberFormatException e) {
                        openErrorDialog("Invalid quantity format");
                    } catch (ApplicationException e) {
                        openErrorDialog("Failed to update quantity: " + e.getMessage());
                    }
                } else {
                    showNoPermissionNotification();
                }
            });
            layout.add(quantityField);
            return layout;
        }).setHeader("Quantity");

        grid.setItems(allP);

        grid.addComponentColumn(product -> {
            Button editButton = new Button("Edit", click -> {
                if (permissionsProfile.hasPermission(storeId, StoreActions.UPDATE_PRODUCT)) {
                    openEditDialog(product);
                } else {
                    showNoPermissionNotification();
                }
            });
            return editButton;
        });
        editDialog = createProductDialog("Edit Product", this::editProduct);
        content.add(editDialog);

        grid.addComponentColumn(product -> {
            Button toggleButton = new Button(products.get(true).contains(product) ? "Disable" : "Enable", click -> {
                StoreActions action = products.get(true).contains(product) ? StoreActions.DISABLE_PRODUCT : StoreActions.ENABLE_PRODUCT;
                if (permissionsProfile.hasPermission(storeId, action)) {
                    try {
                        Endpoints endpoint = getProducts().get(true).contains(product) ? Endpoints.DISABLE_PRODUCT : Endpoints.ENABLE_PRODUCT;
                        ProductRequest request = new ProductRequest(storeId, product.getProductId());
                        appController.postByEndpoint(endpoint, request);
                        refreshGrid();
                    } catch (ApplicationException e) {
                        openErrorDialog(e.getMessage());
                    }
                } else {
                    showNoPermissionNotification();
                }
            });
            return toggleButton;
        });

        grid.addComponentColumn(product -> {
            Button removeButton = new Button("Remove", click -> {
                if (permissionsProfile.hasPermission(storeId, StoreActions.REMOVE_PRODUCT)) {
                    try {
                        ProductRequest request = new ProductRequest(storeId, product.getProductId());
                        appController.postByEndpoint(Endpoints.REMOVE_PRODUCT, request);
                        refreshGrid();
                    } catch (ApplicationException e) {
                        openErrorDialog(e.getMessage());
                    }
                } else {
                    showNoPermissionNotification();
                }
            });
            return removeButton;
        });

        content.add(grid);

        Button addButton = new Button("Add", click -> {
            if (permissionsProfile.hasPermission(storeId, StoreActions.ADD_PRODUCT)) {
                openAddDialog();
            } else {
                showNoPermissionNotification();
            }
        });
        HorizontalLayout addButtonLayout = new HorizontalLayout(addButton);
        addButtonLayout.getStyle().set("justify-content", "center");
        content.add(addButtonLayout);

        addDialog = createProductDialog("Add Product", this::addProduct);
        content.add(addDialog);
    }

    private Dialog createProductDialog(String dialogTitle, Runnable saveAction) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

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

        MultiSelectComboBox<String> keywordsField = new MultiSelectComboBox<>("Keywords");
        binder.bind(keywordsField, "keyWords");
        formLayout.add(keywordsField);

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

    private void editProduct() {
        binder.writeObject(currentProduct);
        try {
            ProductRequest request = new ProductRequest(storeId, currentProduct.getProductId());
            appController.postByEndpoint(Endpoints.UPDATE_PRODUCT, request);
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
            ProductRequest request = new ProductRequest(storeId, currentProduct.getProductId());
            appController.postByEndpoint(Endpoints.ADD_PRODUCT, request);
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

    private void refreshGrid() {
        Map<Boolean, List<Product>> products = getProducts();
        if (products == null || products.isEmpty()) {
        }
        List<Product> allP = new LinkedList<>();
        allP.addAll(products.get(true));
        allP.addAll(products.get(false));
        grid.setItems(allP);
    }

    private void showNoPermissionNotification() {
        Notification.show("You do not have the right permissions to perform this action.");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }
}