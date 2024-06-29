package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import static com.amazonas.common.utils.Rating.FOUR_STARS;

@Route("product-details")
public class ProductDetailsView extends BaseLayout {
    private final AppController appController;

    public ProductDetailsView(AppController appController) {
        super(appController);
        this.appController = appController;

        // Example product details
        Product product = new Product("1","Old Computer", 49.99, "Electronics", "This is a sample product description.", FOUR_STARS);

        // Product name
        H2 productName = new H2(product.productName());

        // Product price
        H3 productPrice = new H3("$" + String.format("%.2f", product.price()));

        // Product category
        Span categoryTitle = new Span("Category: ");
        categoryTitle.getStyle().set("font-weight", "bold");
        Span categoryValue = new Span(product.category());
        HorizontalLayout categoryLayout = new HorizontalLayout(categoryTitle, categoryValue);

        // Product description
        Span descriptionTitle = new Span("Description: ");
        descriptionTitle.getStyle().set("font-weight", "bold");
        Span descriptionValue = new Span(product.description());
        HorizontalLayout descriptionLayout = new HorizontalLayout(descriptionTitle, descriptionValue);

        // Product rating
        HorizontalLayout ratingLayout = new HorizontalLayout();
        int rating = product.rating().ordinal();
        for (int i = 0; i < rating; i++) {
            ratingLayout.add(new Icon(VaadinIcon.STAR));
        }
        int emptyStars = 5 - rating;
        for (int i = 0; i < emptyStars; i++) {
            ratingLayout.add(new Icon(VaadinIcon.STAR_O));
        }

        // Product picture
        Image productImage = new Image();
        productImage.setSrc("data:image/jpeg;base64,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"); // Replace with a real image URL
        productImage.setAlt("Product Image");
        productImage.setWidth("300px");
        productImage.setHeight("300px");

        // Left side layout
        VerticalLayout leftLayout = new VerticalLayout(productName, productPrice, ratingLayout, categoryLayout, descriptionLayout);
        leftLayout.setSpacing(true);
        leftLayout.setPadding(true);
        leftLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.START);

        // Main layout
        HorizontalLayout mainLayout = new HorizontalLayout(leftLayout, productImage);
        mainLayout.setSpacing(true);
        mainLayout.setPadding(true);
        mainLayout.setWidthFull();
        mainLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        // Add to content
        content.add(mainLayout);
    }
}
