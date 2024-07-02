package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;



@Route("previous-orders")
public class PreviousOrdersView extends BaseLayout{
    private final AppController appController;
    private VirtualList<> transactionList;
    public PreviousOrdersView(AppController appController) {
        super(appController);
        String message = appController.getPreviousOrdersMessage();
        H2 h1 = new H2(message);
        h1.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        content.add(h1);
        this.appController = appController;
    }
}
