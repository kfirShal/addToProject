package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;

@Route("storemanagement")
public class StoreManagement extends BaseLayout {

    private final AppController appController;

    public StoreManagement(AppController appController) {
        super(appController);
        this.appController = appController;

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
                getUI().ifPresent(ui -> ui.navigate(ManageInventory.class));
            }
            if ("Manage Store Officials".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(ManageStoreOfficials.class));
            }
            if ("Purchase & Discount Policy".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(PurchaseAndDiscount.class));
            }
            if ("View Purchase History".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(PurchaseHistory.class));
            }
            if ("Close & Reopen Store".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(CloseAndReopenStore.class));
            }
        });

        content.add(grid);
    }
}

// TODO:
//