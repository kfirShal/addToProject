package com.amazonas.frontend.view;

import com.amazonas.common.utils.Pair;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import java.util.Arrays;
import java.util.List;

@Route("storemanagement")
public class StoreManagement extends BaseLayout implements BeforeEnterObserver {

    private final AppController appController;

    public StoreManagement(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    private void createView() {
        String storeId = getParam("storeid");

        // Set the window title
        String newTitle = "Store Management";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);

        // Create a grid with 5 items
        Grid<String> grid = new Grid<>();
        List<String> items = Arrays.asList("Manage Inventory", "Manage Store Officials", "Purchase & Discount Policy", "View Purchase History", "Close & Reopen Store");

        grid.setItems(items);
        grid.addColumn(item -> item).setHeader("Operations");

        grid.addItemClickListener(event -> {
            String item = event.getItem();
            // Navigate to a new view based on the item clicked
            if ("Manage Inventory".equals(item)) {
                String url = getPath("manageinventory", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
            if ("Manage Store Officials".equals(item)) {
                String url = getPath("managestoreofficials", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
            if ("Purchase & Discount Policy".equals(item)) {
                String url = getPath("purchaseanddiscout", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
            if ("View Purchase History".equals(item)) {
                String url = getPath("purchasehistory", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
            if ("Close & Reopen Store".equals(item)) {
                String url = getPath("closeandreopen", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
        });

        content.add(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }
}
