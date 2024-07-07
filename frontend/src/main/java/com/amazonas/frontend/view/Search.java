package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
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
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Route("search")
public class Search extends BaseLayout implements BeforeEnterObserver{
    private final AppController appController;
    private final Grid<Product> productGrid;
    private final Grid<StoreDetails> storeGrid;
    private final Div resultsLayout;
    private final TextField searchField;
    private Div noResults;

    public Search(AppController appController) {
        super(appController);
        this.appController = appController;

        // Main layout setup
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        mainLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        noResults = new Div();
        resultsLayout = new Div();
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
        searchField.addKeyPressListener(Key.ENTER, _ -> performSearch(searchField.getValue()));

        // add also button to click instead of pressing enter
        Button searchButton = new Button("Search", _ -> performSearch(searchField.getValue()));

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
        String searchKey = queryParameters.getParameters().getOrDefault("search", List.of()).stream().findFirst().orElse(null);

        if (searchKey != null) {
            performSearch(searchKey);
            searchField.setValue(searchKey);
        } else {
            System.out.println("No search parameter");
        }
    }

    private void performSearch(String keywords) {
        List<Product> products = appController.searchProducts(keywords);
        List<StoreDetails> stores = appController.searchStores(keywords);
        // navigate to the search page with the search parameter
        UI.getCurrent().navigate("search?search=" + keywords);
        // if no results, display a message
        if (products == null || products.isEmpty()) {
            productGrid.setVisible(false);
            storeGrid.setVisible(false);
            storeGrid.setVisible(false);
            noResults.setText("No results found");
            return;
        }

        // Display results
        noResults.setText("");
        productGrid.setItems(products);
        productGrid.setVisible(true);

        if (stores == null || stores.isEmpty()) {
            storeGrid.setVisible(false);
            return;
        }
        storeGrid.setItems(stores);
        storeGrid.setVisible(true);

    }
}
