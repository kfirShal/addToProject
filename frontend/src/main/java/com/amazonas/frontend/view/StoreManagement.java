package com.amazonas.frontend.view;

import com.amazonas.common.utils.Pair;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.*;

import java.util.Arrays;
import java.util.List;

@Route("storemanagement")
public class StoreManagement extends BaseLayout implements BeforeEnterObserver {

    private final AppController appController;
    private String storeId;

    public StoreManagement(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);

        // Check if the params map contains the key "storeid"
        if (params.getParameters().containsKey("storeid")) {
            List<String> storeIdList = params.getParameters().get("storeid");

            // Ensure the list is not null or empty before accessing its elements
            if (storeIdList != null && !storeIdList.isEmpty()) {
                storeId = storeIdList.get(0);
                createView();
            } else {
                // Handle the case where the list is empty
                // You may want to log an error or redirect to an error page
                System.err.println("Error: 'storeid' parameter list is empty.");
            }
        } else {
            // Handle the case where the key "storeid" does not exist
            // You may want to log an error or redirect to an error page
            System.err.println("Error: 'storeid' parameter is missing.");
        }
    }

    private void createView() {
        // Set the window title
        H2 title = new H2("Store Management");
        title.getStyle().set("align-self", "center");
        content.add(title);

        // Create a grid with 5 items
        Grid<String> grid = new Grid<>();
        List<String> items = Arrays.asList("Manage Inventory", "Manage Store Officials",
                "Purchase & Discount Policy", "View Purchase History", "Close & Reopen Store");

        grid.setItems(items);
        grid.addColumn(item -> item).setHeader("Operations");

        grid.addItemClickListener(event -> {
            String item = event.getItem();
            navigateToSubView(item);
        });

        content.add(grid);
    }

    private void navigateToSubView(String item) {
        String basePath = "";
        switch (item) {
            case "Manage Inventory":
                basePath = "manageinventory";
                break;
            case "Manage Store Officials":
                basePath = "managestoreofficials";
                break;
            case "Purchase & Discount Policy":
                basePath = "purchaseanddiscount";
                break;
            case "View Purchase History":
                basePath = "purchasehistory";
                break;
            case "Close & Reopen Store":
                basePath = "closeandreopenstore";
                break;
        }
        String url = getPath(basePath, Pair.of("storeid", storeId));
        getUI().ifPresent(ui -> ui.navigate(url));
    }
}
