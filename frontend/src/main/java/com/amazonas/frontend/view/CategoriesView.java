package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.router.Route;

@Route("Categories")
public class CategoriesView extends BaseLayout {
    public CategoriesView(AppController appController) {
        super(appController);
    }
}

