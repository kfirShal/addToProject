package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.requests.stores.GlobalSearchRequest;
import com.amazonas.common.requests.stores.ProductSearchRequestBuilder;
import com.amazonas.common.requests.stores.StoreDetailsRequestBuilder;
import com.amazonas.common.requests.stores.StoreSearchRequest;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route("")
public class WelcomeView extends BaseLayout {

    private final AppController appController;

    public WelcomeView(AppController appController) {
        super(appController);
//        String message = appController.getWelcomeMessage();
//        H2 h1 = new H2(message);
//        h1.getStyle().set("text-align", "center");
//        content.add(h1);
        this.appController = appController;
        addStoresAndProducts();
    }

    private void addStoresAndProducts() {
        GlobalSearchRequest productRequest = new GlobalSearchRequest(Rating.NOT_RATED, ProductSearchRequestBuilder.create().setKeyWords(Collections.singletonList("1")).build());
        StoreSearchRequest storeRequest = StoreDetailsRequestBuilder.create().build();

        List<StoreDetails> stores;
        List<Product> products;
        try {
            products = appController.postByEndpoint(Endpoints.SEARCH_PRODUCTS_GLOBALLY, productRequest);
            stores = appController.postByEndpoint(Endpoints.SEARCH_STORES_GLOBALLY, storeRequest);

        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }

        H1 storeTitle = new H1("Stores");
        storeTitle.getStyle().set("margin-top", "20px");
        if(stores == null)
            stores = new ArrayList<>();
        // send max 5 stores
        Div storeGrid = createStoreGrid(stores.subList(0, Math.min(5, stores.size())));
        H1 productTitle = new H1("Products");
        productTitle.getStyle().set("margin-top", "20px");
        if(products == null)
            products = new ArrayList<>();
        // send max 5 products
        Div productGrid = createProductGrid(products.subList(0, Math.min(5, products.size())));

        content.add(storeTitle, storeGrid, productTitle, productGrid);
    }

    private Div createStoreGrid(List<StoreDetails> stores) {
        Div storeGrid = new Div();
        storeGrid.getStyle().set("display", "flex");
        storeGrid.getStyle().set("flex-wrap", "wrap");
        storeGrid.getStyle().set("gap", "20px");

        for (StoreDetails store : stores) {
            Div storeCard = new Div();
            storeCard.getStyle().set("width", "200px");
            storeCard.getStyle().set("height", "200px");
            storeCard.getStyle().set("border", "1px solid #ccc");
            storeCard.getStyle().set("border-radius", "8px");
            storeCard.getStyle().set("padding", "10px");
            storeCard.getStyle().set("text-align", "center");
            storeCard.getStyle().set("cursor", "pointer");

            Icon storeImage = VaadinIcon.SHOP.create();
            storeImage.getStyle().set("width", "100px");
            storeImage.getStyle().set("height", "100px");

            Span storeName = new Span(store.getStoreName());
            Span storeDescription = new Span(store.getStoreDescription());
            // add each one in new line
            storeName.getStyle().set("display", "block");
            storeDescription.getStyle().set("display", "block");

            storeCard.add(storeImage, storeName, storeDescription);

            storeCard.addClickListener(_ -> UI.getCurrent().navigate("store?storeid=" + store.getStoreId()));

            storeGrid.add(storeCard);
        }

        return storeGrid;
    }

    private Div createProductGrid(List<Product> products) {
        Div productGrid = new Div();
        productGrid.getStyle().set("display", "flex");
        productGrid.getStyle().set("flex-wrap", "wrap");
        productGrid.getStyle().set("gap", "20px");

        for (Product product : products) {
            Div productCard = new Div();
            productCard.getStyle().set("width", "200px");
            productCard.getStyle().set("height", "200px");
            productCard.getStyle().set("border", "1px solid #ccc");
            productCard.getStyle().set("border-radius", "8px");
            productCard.getStyle().set("padding", "10px");
            productCard.getStyle().set("text-align", "center");
            productCard.getStyle().set("cursor", "pointer");

            Icon productImage = VaadinIcon.BULLSEYE.create();
            productImage.getStyle().set("width", "100px");
            productImage.getStyle().set("height", "100px");

            Span productName = new Span(product.getProductName());
            Span productPrice = new Span("Price: $" + product.getPrice());
            Span productCategory = new Span("Category: " + product.getCategory());
            Span productDescription = new Span(product.getDescription());
            // add each one in new line
            productName.getStyle().set("display", "block");
            productPrice.getStyle().set("display", "block");
            productCategory.getStyle().set("display", "block");
            productDescription.getStyle().set("display", "block");

            productCard.add(productImage, productName, productPrice, productCategory, productDescription);

            productCard.addClickListener(_ -> UI.getCurrent().navigate("product-details?productId=" + product.getProductId()));

            productGrid.add(productCard);
        }

        return productGrid;
    }
}
