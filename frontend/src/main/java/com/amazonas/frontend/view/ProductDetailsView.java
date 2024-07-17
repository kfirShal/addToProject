package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.requests.users.CartRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.List;

@Route("product-details")
public class ProductDetailsView extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;

    public ProductDetailsView(AppController appController) {
        super(appController);
        this.appController = appController;

    }

    
    private void createProductLayout() {
        // Get product id from the URL
        String productId = getParam("productId");
        Product product = null;
        try {
            List<Product> fetched = appController.postByEndpoint(Endpoints.GET_PRODUCT, productId);
            product = fetched.getFirst();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }

        // Product name
        H2 productName = new H2(product.getProductName());

        // Product price
        H3 productPrice = new H3("$" + String.format("%.2f", product.getPrice()));

        // Product category
        Span categoryTitle = new Span("Category: ");
        categoryTitle.getStyle().set("font-weight", "bold");
        Span categoryValue = new Span(product.getCategory());
        HorizontalLayout categoryLayout = new HorizontalLayout(categoryTitle, categoryValue);

        // Product description
        Span descriptionTitle = new Span("Description: ");
        descriptionTitle.getStyle().set("font-weight", "bold");
        Span descriptionValue = new Span(product.getDescription());
        HorizontalLayout descriptionLayout = new HorizontalLayout(descriptionTitle, descriptionValue);

        // Product rating
        HorizontalLayout ratingLayout = new HorizontalLayout();
        int rating = product.getRating().ordinal();
        for (int i = 0; i < rating; i++) {
            ratingLayout.add(new Icon(VaadinIcon.STAR));
        }
        int emptyStars = 5 - rating;
        for (int i = 0; i < emptyStars; i++) {
            ratingLayout.add(new Icon(VaadinIcon.STAR_O));
        }

        // Quantity selector
        IntegerField quantityField = new IntegerField();
        quantityField.setValue(1);
        quantityField.setMin(1);
        quantityField.setWidth("60px"); // Adjusted width for smaller size

        // Buttons for quantity control
        Button increaseButton = new Button(new Icon(VaadinIcon.PLUS));
        increaseButton.addClickListener(event -> {
            int currentValue = quantityField.getValue();
            quantityField.setValue(currentValue + 1);
        });

        Button decreaseButton = new Button(new Icon(VaadinIcon.MINUS));
        decreaseButton.addClickListener(event -> {
            int currentValue = quantityField.getValue();
            if (currentValue > 1) {
                quantityField.setValue(currentValue - 1);
            }
        });

        // Add to Cart button
        Button addToCartButton = new Button("Add product to cart", new Icon(VaadinIcon.CART));
        Product finalProduct = product;
        addToCartButton.addClickListener(event -> {
            int quantity = quantityField.getValue();
            CartRequest cartRequest = new CartRequest(finalProduct.getStoreId(), finalProduct.getProductId(), quantity);
            try {
                appController.postByEndpoint(Endpoints.ADD_PRODUCT_TO_CART, cartRequest);
                showNotification("Product added to cart successfully! with quantity: " + quantity);
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        });

        // Back to store button
        Button backButton = new Button("Back to Store");
        backButton.addClickListener(event -> UI.getCurrent().navigate("store")); // Navigate back to store view
        // Layout for quantity selector and add to cart button and back button
        HorizontalLayout quantityLayout = new HorizontalLayout(decreaseButton, quantityField, increaseButton, addToCartButton, backButton);
        quantityLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        // Product picture
        Image productImage = new Image();
        productImage.setSrc("data:image/jpeg;base64,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"); // Path relative to the 'frontend' directory
        productImage.setAlt("Product Image");
        productImage.setWidth("300px");
        productImage.setHeight("300px");
        // Add margin to bring the image closer to the left layout
        productImage.getStyle().set("margin-left", "-100px"); // Adjust the negative value as needed
        // Left side layout
        VerticalLayout leftLayout = new VerticalLayout(productName, productPrice, ratingLayout, categoryLayout, descriptionLayout, quantityLayout);
        leftLayout.setSpacing(true);
        leftLayout.setPadding(true);
        leftLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        // Main layout with adjusted spacing
        HorizontalLayout mainLayout = new HorizontalLayout(leftLayout, productImage);
        //mainLayout.setSpacing(false); // Remove spacing between the layouts
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START); // Align items to start

        content.add(mainLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createProductLayout();
    }
}
