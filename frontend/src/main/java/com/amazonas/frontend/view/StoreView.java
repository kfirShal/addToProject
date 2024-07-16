package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.utils.Pair;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.model.FrontendStore;
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
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.Map;

@Route("store")
public class StoreView extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;

    public StoreView(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    private void createView() {
        String storeId = getParam("storeid");
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
            List<Map<Boolean,List<Product>>> fetched = appController.postByEndpoint(Endpoints.GET_STORE_PRODUCTS, storeId);
            products = fetched.getFirst().get(true);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }

        FrontendStore store = new FrontendStore(storeDetails.storeName(), storeDetails.storeDescription(), storeDetails.storeRating(), products);


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
            nameDiv.add(new Span(product.getProductName()));

            Div priceDiv = new Div();
            priceDiv.add(new Span("$" + String.format("%.2f", product.getPrice())));

            HorizontalLayout productRatingLayout = new HorizontalLayout();
            int productRating = product.getRating().ordinal();
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

            Button productButton = new Button(product.getProductName(), event -> {
                String url = getPath("product-details", Pair.of("productId", product.getProductId()));
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

        if(permissionsProfile.getStoreIds().contains(storeId) || permissionsProfile.hasPermission(MarketActions.ALL)){
            // Manage Store button
            Button manageStoreButton = new Button("Manage Store");
            manageStoreButton.addClickListener(event -> {
                String url = getPath("storemanagement", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            });
            content.add(manageStoreButton);
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }
}
