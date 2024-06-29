package com.amazonas.frontend.view;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.model.Store;
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

import java.util.List;

@Route("store")
public class StoreView extends BaseLayout {
    private final AppController appController;

    public StoreView(AppController appController) {
        super(appController);
        this.appController = appController;

        // Example store details
        List<Product> products = List.of(
                new Product("1", "Product 1", 19.99, "Category 1", "Description for product 1.", Rating.FOUR_STARS),
                new Product("2", "Product 2", 29.99, "Category 2", "Description for product 2.", Rating.THREE_STARS)
        );
        Store store = new Store("Sample Store", "This is a sample store description.", Rating.FIVE_STARS, products);

        // Store name
        H2 storeName = new H2(store.storeName());

        // Store description
        Paragraph storeDescription = new Paragraph(store.description());

        // Store rating
        HorizontalLayout ratingLayout = new HorizontalLayout();
        int rating = store.rating().ordinal() + 1;
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

        // Products list
        VirtualList<Product> productList = new VirtualList<>();
        productList.setHeight("400px");
        productList.setRenderer(new ComponentRenderer<>(product -> {
            Div nameDiv = new Div();
            nameDiv.add(new Span(product.productName()));

            Div priceDiv = new Div();
            priceDiv.add(new Span("$" + String.format("%.2f", product.price())));

            HorizontalLayout productRatingLayout = new HorizontalLayout();
            int productRating = product.rating().ordinal() + 1;
            for (int i = 0; i < productRating; i++) {
                Icon star = new Icon(VaadinIcon.STAR);
                star.setSize("12px");
                productRatingLayout.add(star);
            }
            int productEmptyStars = 5 - productRating;
            for (int i = 0; i < productEmptyStars; i++) {
                Icon star = new Icon(VaadinIcon.STAR_O);
                star.setSize("12px");
                productRatingLayout.add(star);
            }

            Button productButton = new Button(product.productName(), event -> {
                getUI().ifPresent(ui -> ui.navigate("ProductDetailsView.class"));
            });

            VerticalLayout itemLayout = new VerticalLayout(productButton, priceDiv, productRatingLayout);
            itemLayout.setPadding(true);
            itemLayout.setWidthFull();

            return itemLayout;
        }));
        productList.setItems(store.products());

        // Add components to the layout
        VerticalLayout layout = new VerticalLayout(storeName, storeDescription, ratingLayout, productList);
        layout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.setWidth("80%");
        layout.getStyle().set("margin", "auto");

        // Add to content
        content.add(layout);
    }
}
