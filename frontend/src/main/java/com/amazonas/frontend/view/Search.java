package com.amazonas.frontend.view;

import com.amazonas.common.requests.stores.GlobalSearchRequest;
import com.amazonas.common.requests.stores.ProductSearchRequestBuilder;
import com.amazonas.common.requests.stores.StoreDetailsRequestBuilder;
import com.amazonas.common.requests.stores.StoreSearchRequest;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route("search")
public class Search extends BaseLayout implements BeforeEnterObserver{
    private final AppController appController;
    private final Grid<Product> productGrid;
    private final Grid<StoreDetails> storeGrid;
    private final TextField searchField;
    private final Div noResults;

    public Search(AppController appController) {
        super(appController);
        this.appController = appController;

        // Main layout setup
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        noResults = new Div();
        Div resultsLayout = new Div();
        resultsLayout.setWidth("100%");

        // Results grids
        productGrid = new Grid<>(Product.class);
        productGrid.setColumns("productId", "productName", "price", "category", "description", "rating", "keyWords");
        // when click on item go to product-details?productId= + productId
        productGrid.addItemClickListener(event -> UI.getCurrent().navigate("product-details?productId=" + event.getItem().getProductId()));
        storeGrid = new Grid<>(StoreDetails.class);
        storeGrid.setColumns("storeId", "storeName", "storeRating", "storeDescription");
        // when click on item go to store?storeid= + storeId
        storeGrid.addItemClickListener(event -> UI.getCurrent().navigate("store?storeid=" + event.getItem().getStoreId()));

        resultsLayout.add(productGrid, storeGrid);

        // Search button with text field to enter search keywords and search
        searchField = new TextField();
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.addKeyPressListener(Key.ENTER, _ -> {
            String input = searchField.getValue();
            String keywords = String.join("+", input.split("\\s+")); // Join keywords with '+' for URL
            UI.getCurrent().navigate("search?search=" + keywords);
        });

        Button searchButton = new Button("Search", _ -> {
            String input = searchField.getValue();
            String keywords = String.join("+", input.split("\\s+")); // Join keywords with '+' for URL
            UI.getCurrent().navigate("search?search=" + keywords);
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(searchField, searchButton);
        buttonLayout.setWidth("100%");
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        // Add components to main layout
        mainLayout.add(buttonLayout, resultsLayout);
        resultsLayout.add(noResults);
        content.add(mainLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        QueryParameters queryParameters = event.getLocation().getQueryParameters();
        List<String> searchKey = queryParameters.getParameters().getOrDefault("search", List.of());
        // Join the search keywords with spaces to set in the search field
        String searchKeywords = String.join(" ", searchKey);
        searchField.setValue(searchKeywords);
        try {
            performSearch(searchKey);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }
    }

    private void performSearch(List<String> keywords) throws ApplicationException {
        // print the search keywords
        if (keywords.isEmpty()) {
            return; // Optionally handle empty search
        }
        // split by spaces or + to get individual keywords
        List<String> searchKeywords = new ArrayList<>();
        for (String keyword : keywords) {
            searchKeywords.addAll(Arrays.asList(keyword.split("\\s+|\\+")));
        }
        GlobalSearchRequest productRequest = new GlobalSearchRequest(Rating.NOT_RATED, ProductSearchRequestBuilder.create().setKeyWords(searchKeywords).build());
        List<Product> products = appController.postByEndpoint(Endpoints.SEARCH_PRODUCTS_GLOBALLY, productRequest);
        StoreSearchRequest storeRequest = StoreDetailsRequestBuilder.create().setStoreName(String.join(" ", searchKeywords)).build();
        List<StoreDetails> stores = appController.postByEndpoint(Endpoints.SEARCH_STORES_GLOBALLY, storeRequest);
        // navigate to the search page with the search parameter
        // if no results, display a message
        boolean noProducts = false, noStores = false;
        if (products == null || products.isEmpty()) {
            noProducts = true;
        } else {
            productGrid.setItems(products);
        }

        if (stores == null || stores.isEmpty()) {
            noStores = true;
        } else {
            storeGrid.setItems(stores);
        }

        if(noProducts && noStores){
            noResults.setText("No results found");
            return;
        }

        // Display results
        noResults.setText("");
        productGrid.setVisible(!noProducts);
        storeGrid.setVisible(!noStores);
    }
}
