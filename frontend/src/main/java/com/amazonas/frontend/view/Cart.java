package com.amazonas.frontend.view;

import com.amazonas.common.dtos.ShoppingCart;
import com.amazonas.common.dtos.StoreBasket;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.stores.ProductRequest;
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
    protected List<Product> items;
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
        try {
            configureGrid();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

        // add in the bottom right button for proceed to checkout
        Button checkoutButton = new Button("Checkout", _ -> UI.getCurrent().navigate("Payment"));
        checkoutButton.setIcon(VaadinIcon.CHECK.create());
        checkoutButton.getStyle().set("margin-left", "auto");
        content.add(grid, checkoutButton);
    }

    private void configureGrid() throws ApplicationException {
        String userId = AppController.getCurrentUserId();
        System.out.println("userId: " + userId);
        this.cart = (ShoppingCart) appController.postByEndpoint(Endpoints.VIEW_CART, null);
        Map<String, StoreBasket> baskets = cart.getBaskets(); // storeId -> StoreBasket

        for (Map.Entry<String, StoreBasket> entry : baskets.entrySet()) {
            String storeId = entry.getKey();
            StoreBasket storeBasket = entry.getValue();

            // Create a sub-grid for each store's basket
            Grid<Map.Entry<String, Integer>> productGrid = new Grid<>((Class<Map.Entry<String, Integer>>)(Class<?>)Map.Entry.class, false);
            productGrid.addColumn(Map.Entry::getKey).setHeader("Product ID");
            productGrid.addColumn(Map.Entry::getValue).setHeader("Quantity");

            productGrid.addColumn(new ComponentRenderer<>(item -> {
                Button removeButton = new Button("Remove", VaadinIcon.TRASH.create());
                removeButton.addClickListener(click -> {
                    storeBasket.getProducts().remove(item.getKey());
                    productGrid.setItems(storeBasket.getProducts().entrySet());
                });
                return removeButton;
            })).setHeader("Actions");

            productGrid.setItems(storeBasket.getProducts().entrySet());

            // Add the store name as a header
            Paragraph storeHeader = new Paragraph("Store: " + storeId);
            content.add(storeHeader, productGrid);

            productGrid.addItemClickListener(event -> {
                try {
                    Product product = getProduct(storeId, event.getItem().getKey());
                    UI.getCurrent().navigate("Product/" + product.getProductId());
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Product getProduct(String storeId, String productId) throws ApplicationException {
        ProductRequest productRequest = new ProductRequest(storeId, productId);
        List<Product> products = appController.postByEndpoint(Endpoints.GET_PRODUCT, productRequest);
        if (products.isEmpty()) {
            throw new ApplicationException("Product not found");
        }
        return products.get(0);
    }
}
