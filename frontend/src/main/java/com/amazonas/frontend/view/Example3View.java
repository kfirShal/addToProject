package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

@Route("example3")
public class Example3View extends BaseLayout {

    private final AppController appController;

    public Example3View(AppController appController) {
        super(appController);
        String message = appController.getExampleMessage(3);
        H2 h1 = new H2(message);
        h1.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        content.add(h1);

        this.appController = appController;
    }
}
