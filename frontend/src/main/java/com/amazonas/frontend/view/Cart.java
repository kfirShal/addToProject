package com.amazonas.frontend.view;

import com.amazonas.common.dtos.ShoppingCart;
import com.amazonas.common.dtos.StoreBasket;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.requests.stores.ProductRequest;
import com.amazonas.common.requests.users.CartRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.amazonas.common.dtos.Product;

import java.util.List;
import java.util.Map;

import static com.amazonas.frontend.control.AppController.isUserLoggedIn;

@Route("cart")
public class Cart extends Profile {
    protected ShoppingCart cart;
    protected Grid<ShoppingCart> grid;
    private AppController appController;

    public Cart(AppController appController) {
        super(appController);
        this.appController = appController;

        // check if user logged in, if not return to home page
        if (!isUserLoggedIn()) {
            UI.getCurrent().navigate("");
            return;
        }

        grid = new Grid<>(ShoppingCart.class, false);
        // if empty hide the grid
        configureGrid();

        // add in the bottom right button for proceed to checkout
        Button checkoutButton = new Button("Checkout", _ -> UI.getCurrent().navigate("Payment"));
        checkoutButton.setIcon(VaadinIcon.CHECK.create());
        checkoutButton.getStyle().set("margin-left", "auto");
        content.add(grid, checkoutButton);
    }

    private void configureGrid() {
        String userId = AppController.getCurrentUserId();
        List<ShoppingCart> fetched = null;
        try {
            fetched = appController.postByEndpoint(Endpoints.VIEW_CART, null);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }
        this.cart = fetched.getFirst();
        Map<String, StoreBasket> baskets = cart.getBaskets(); // storeId -> StoreBasket


        for (Map.Entry<String, StoreBasket> entry : baskets.entrySet()) {
            String storeId = entry.getKey();
            StoreBasket storeBasket = entry.getValue();

            // Check if the storeBasket is empty, if so, skip this iteration
            if (storeBasket.getProducts().isEmpty()) {
                continue; // Skip this store as it has no products in the cart
            }

            // Create a sub-grid for each store's basket
            Grid<Map.Entry<String, Integer>> productGrid = new Grid<>((Class<Map.Entry<String, Integer>>)(Class<?>)Map.Entry.class, false);
            // show product name using getProduct
            productGrid.addColumn(entry1 -> {
                try {
                    Product product = getProduct(storeId, entry1.getKey());
                    return product.getProductName();
                } catch (ApplicationException e) {
                    e.printStackTrace();
                    return "Product not found";
                }
            }).setHeader("Product Name");
            productGrid.addColumn(Map.Entry::getValue).setHeader("Quantity");

            // Add change quantity button + and -
            productGrid.addColumn(new ComponentRenderer<>(entry1 -> {
                Button incrementButton = new Button("+", event -> {
                    changeProductQuantity(userId, storeId, entry1.getKey(), entry1.getValue() + 1);
                });
                Button decrementButton = new Button("-", event -> {
                    // if 1 then remove
                    if (entry1.getValue() == 1) {
                        removeProductFromCart(userId, storeId, entry1.getKey());
                        return;
                    }
                    changeProductQuantity(userId, storeId, entry1.getKey(), entry1.getValue() - 1);
                });
                return new Paragraph(incrementButton, decrementButton);
            })).setHeader("Change Quantity");

            // Add remove product button
            productGrid.addColumn(new ComponentRenderer<>(entry1 -> {
                return new Button("Remove", event -> {
                    removeProductFromCart(userId, storeId, entry1.getKey());
                });
            })).setHeader("Remove");

            productGrid.setItems(storeBasket.getProducts().entrySet());

            // Add the store name as a header
            String storeName = getStoreName(storeId);
            Paragraph storeHeader = new Paragraph("Store: " + storeName);
            // when click go to store page - store?storeid= + storeId
            storeHeader.addClickListener(event -> UI.getCurrent().navigate("store?storeid=" + storeId));
            content.add(storeHeader, productGrid);

            productGrid.addItemClickListener(event -> {
                try {
                    Product product = getProduct(storeId, event.getItem().getKey());
                    UI.getCurrent().navigate("product-details?productId=" + product.getProductId());
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void changeProductQuantity(String userId, String storeId, String productId, int quantity) {
        try {
            CartRequest request = new CartRequest(storeId, productId, quantity);
            appController.postByEndpoint(Endpoints.CHANGE_PRODUCT_QUANTITY, request);
            UI.getCurrent().getPage().reload();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private void removeProductFromCart(String userId, String storeId, String productId) {
        try {
            CartRequest request = new CartRequest(storeId, productId, 0);
            appController.postByEndpoint(Endpoints.REMOVE_PRODUCT_FROM_CART, request);
            UI.getCurrent().getPage().reload();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private String getStoreName(String storeID) {
        try {
            StoreDetails store = (StoreDetails) appController.postByEndpoint(Endpoints.GET_STORE_DETAILS, storeID).getFirst();
            return store.getStoreName();
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    private Product getProduct(String storeId, String productId) throws ApplicationException {
        List<Product> products = appController.postByEndpoint(Endpoints.GET_PRODUCT, productId);
        if (products.isEmpty()) {
            throw new ApplicationException("Product not found");
        }
        return products.getFirst();
    }
}
