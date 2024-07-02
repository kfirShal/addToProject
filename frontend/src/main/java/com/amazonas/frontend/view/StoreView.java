package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.common.requests.stores.ProductRequest;
import com.amazonas.common.utils.Pair;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.model.Store;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;

import java.util.ArrayList;
import java.util.List;

@Route("store")
public class StoreView extends BaseLayout {
    private final AppController appController;

    public StoreView(AppController appController) {
        super(appController);
        this.appController = appController;

        // TODO :Get store id from the URL
        String storeId = getParam("storeId");
        StoreDetails storeDetails = null;
        try {
            List<StoreDetails> fetched = appController.postByEndpoint(Endpoints.GET_STORE_DETAILS, storeId);
            storeDetails = fetched.getFirst();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }

        // Get list of products from the backend
        List<Product> products = new ArrayList<>();
        try {
            products = appController.postByEndpoint(Endpoints.GET_STORE_PRODUCTS,storeId);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }

        Store store = new Store(storeDetails.storeName(), storeDetails.storeDescription(), storeDetails.storeRating(), products);


//        // Example store details
//        List<Product> products = List.of(
//                new Product("1", "Product 1", 19.99, "Category 1", "Description for product 1.", Rating.FOUR_STARS, "store1"),
//                new Product("2", "Product 2", 29.99, "Category 2", "Description for product 2.", Rating.THREE_STARS, "store1"),
//                new Product("3", "Product 3", 39.99, "Category 3", "Description for product 3.", Rating.FIVE_STARS, "store1"),
//                new Product("4", "Product 4", 49.99, "Category 4", "Description for product 4.", Rating.TWO_STARS, "store1"),
//                new Product("5", "Product 5", 59.99, "Category 5", "Description for product 5.", Rating.ONE_STAR, "store1"),
//                new Product("6", "Product 6", 69.99, "Category 6", "Description for product 6.", Rating.FOUR_STARS, "store1")
//        );
//        Store store = new Store("Sample Store", "This is a sample store description.", Rating.FIVE_STARS, products);

        // Store name
        H2 storeName = new H2(store.storeName());

        // Store description
        Paragraph storeDescription = new Paragraph(store.description());

        // Store rating
        HorizontalLayout ratingLayout = new HorizontalLayout();
        int rating = store.rating().ordinal();
        for (int i = 0; i < rating; i++) {
            Icon star = new Icon(VaadinIcon.STAR);
            star.setSize("16px");
            ratingLayout.add(star);
        }
        int emptyStars = 5 - rating;
        for (int i = 0; i < emptyStars; i++) {
            Icon star = new Icon(VaadinIcon.STAR_O);
            star.setSize("16px");
            ratingLayout.add(star);
        }

        // Products grid
        VerticalLayout productsLayout = new VerticalLayout();
        productsLayout.setWidthFull();

        int columns = 5; // Number of columns

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            Div nameDiv = new Div();
            nameDiv.add(new Span(product.productName()));

            Div priceDiv = new Div();
            priceDiv.add(new Span("$" + String.format("%.2f", product.price())));

            HorizontalLayout productRatingLayout = new HorizontalLayout();
            int productRating = product.rating().ordinal();
            for (int j = 0; j < productRating; j++) {
                Icon star = new Icon(VaadinIcon.STAR);
                star.setSize("12px");
                productRatingLayout.add(star);
            }
            int productEmptyStars = 5 - productRating;
            for (int j = 0; j < productEmptyStars; j++) {
                Icon star = new Icon(VaadinIcon.STAR_O);
                star.setSize("12px");
                productRatingLayout.add(star);
            }

            Button productButton = new Button(product.productName(), event -> {
                String url = getPath("product-details", Pair.of("productId", product.productId()));
//                String url = String.format("product-details?productId=%s&productName=%s&productPrice=%s&productCategory=%s&productDescription=%s&productRating=%s&storeId=%s",
//                        product.productId(), product.productName(), product.price(), product.category(), product.description(), product.rating().name(), product.storeId());
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

        // Add components to the layout
        VerticalLayout layout = new VerticalLayout(storeName, storeDescription, ratingLayout, productsLayout);
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setWidth("80%");
        layout.getStyle().set("margin", "auto");

        // Add to content
        content.add(layout);
    }

}
