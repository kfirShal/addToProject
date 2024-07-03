package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.router.Route;

@Route("Products")
public class ProductsView extends BaseLayout {
    public ProductsView(AppController appController) {
        super(appController);
    }
}

