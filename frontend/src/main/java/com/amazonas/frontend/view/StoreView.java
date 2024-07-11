package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.stores.SearchInStoreRequest;
import com.amazonas.common.requests.stores.SearchRequest;
import com.amazonas.common.requests.stores.SearchRequestBuilder;
import com.amazonas.common.utils.Pair;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.model.FrontendStore;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Route("store")
public class StoreView extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;
    private final List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    private VerticalLayout productsLayout;
    private VerticalLayout layout;
    private ProgressBar progressBar;
    private String storeId;

    public StoreView(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    private void createView() {
        storeId = getParam("storeid");
        StoreDetails storeDetails = null;
        try {
            List<StoreDetails> fetched = appController.postByEndpoint(Endpoints.GET_STORE_DETAILS, storeId);
            storeDetails = fetched.getFirst();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }

        // Get list of products from the backend
        List<Product> products = null;
        try {
            List<Map<Boolean, List<Product>>> fetched = appController.postByEndpoint(Endpoints.GET_STORE_PRODUCTS, storeId);
            products = fetched.getFirst().get(true);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }


        allProducts.addAll(products);
        filteredProducts.addAll(products);

        FrontendStore store = new FrontendStore(storeDetails.storeName(), storeDetails.storeDescription(), storeDetails.storeRating(), products);

        // Store name, description, and rating
        H2 storeName = new H2(store.storeName());
        Paragraph storeDescription = new Paragraph(store.description());
        HorizontalLayout ratingLayout = createRatingLayout(store.rating().ordinal());

        // Search bar
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("100%");
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search for product");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchLayout.add(searchField);
        searchField.addKeyPressListener(Key.ENTER, event -> {
            String query = searchField.getValue().toLowerCase();
            filterProducts(query);
            VerticalLayout updatedProductsLayout = updateProductLayout();
            layout.replace(productsLayout, updatedProductsLayout);
            productsLayout = updatedProductsLayout;
        });

        // Loading spinner
        progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        // Top layout
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidthFull();
        topLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout storeInfoLayout = new VerticalLayout(storeName, storeDescription, ratingLayout);
        storeInfoLayout.setAlignItems(FlexComponent.Alignment.START);

        topLayout.add(storeInfoLayout, searchLayout);

        // Products grid
        productsLayout = updateProductLayout();

        // Add components to the layout
        layout = new VerticalLayout(topLayout, progressBar, productsLayout);
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setWidth("80%");
        layout.getStyle().set("margin", "auto");

        // Add to content
        content.add(layout);
    }

    private HorizontalLayout createRatingLayout(int rating) {
        HorizontalLayout ratingLayout = new HorizontalLayout();
        for (int i = 0; i < rating; i++) {
            Icon star = new Icon(VaadinIcon.STAR);
            star.setSize("16px");
            ratingLayout.add(star);
        }
        for (int i = 0; i < 5 - rating; i++) {
            Icon star = new Icon(VaadinIcon.STAR_O);
            star.setSize("16px");
            ratingLayout.add(star);
        }
        return ratingLayout;
    }

    private VerticalLayout updateProductLayout() {
        VerticalLayout productsLayout = new VerticalLayout();
        productsLayout.setWidthFull();
        productsLayout.setId("products-layout");

        if (filteredProducts.isEmpty()) {
            productsLayout.add(new Paragraph("There are no available products in the store right now :("));
        } else {
            int columns = 5;
            for (int i = 0; i < filteredProducts.size(); i++) {
                Product product = filteredProducts.get(i);

                Div nameDiv = new Div();
                nameDiv.add(new Span(product.productName()));

                Div priceDiv = new Div();
                priceDiv.add(new Span("$" + String.format("%.2f", product.price())));

                HorizontalLayout productRatingLayout = createRatingLayout(product.rating().ordinal());

                Button productButton = new Button(product.productName(), event -> {
                    String url = getPath("product-details", Pair.of("productId", product.productId()));
                    getUI().ifPresent(ui -> ui.navigate(url));
                });

                VerticalLayout productLayout = new VerticalLayout(productButton, priceDiv, productRatingLayout);
                productLayout.setPadding(true);
                productLayout.setSpacing(true);
                productLayout.setWidth("100%");
                productLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

                HorizontalLayout row;
                if (i % columns == 0) {
                    row = new HorizontalLayout();
                    row.setSpacing(true);
                    row.setPadding(true);
                    productsLayout.add(row);
                } else {
                    row = (HorizontalLayout) productsLayout.getComponentAt(productsLayout.getComponentCount() - 1);
                }
                row.add(productLayout);
            }
        }

        return productsLayout;
    }

    private void filterProducts(String query) {
        progressBar.setVisible(true); // Show the progress bar while loading
        filteredProducts.clear();
        if (query.isEmpty()) {
            filteredProducts.addAll(allProducts);
        } else {
            List<String> keywords = Arrays.stream(query.split(" ")).toList();
            SearchRequest request = SearchRequestBuilder.create()
                    .setProductName(query)
                    .setKeyWords(keywords)
                    .setProductCategory(query)
                    .build();
            SearchInStoreRequest searchInStore = new SearchInStoreRequest(storeId, request);
            try{
                filteredProducts = appController.postByEndpoint(Endpoints.SEARCH_PRODUCTS_IN_STORE, searchInStore);
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
                return;
            }

//            for(Product product : allProducts){
//                SearchRequest searchRequest = new SearchRequest(product.productName(),product.keyWords(),0,100,product.category(),product.rating());
//                SearchInStoreRequest searchInStore = new SearchInStoreRequest(storeId, searchReauest);
//
//                try {
//
//                } catch (ApplicationException e) {
//                    openErrorDialog(e.getMessage());
//                }
//            }

//
//            for (Product product : allProducts) {
//                if (product.matchesKeyword(query)) {
//                    filteredProducts.add(product);
//                }
//            }
        }
        progressBar.setVisible(false); // Hide the progress bar after loading
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }
}
