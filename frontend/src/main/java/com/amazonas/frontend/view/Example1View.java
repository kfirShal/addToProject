package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.List;

@Route("example1")
public class Example1View extends BaseLayout {

    private final AppController appController;

    public Example1View(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window title
        String newTitle = "Management Dashboard";
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
                getUI().ifPresent(ui -> ui.navigate(InventoryManagementView.class));
            }
            if ("Manage Store Officials".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(Example3View.class));
            }
            if ("Purchase & Discount Policy".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(Example4View.class));
            }
            if ("View Purchase History".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(Example5View.class));
            }
            if ("Close & Reopen Store".equals(item)) {
                getUI().ifPresent(ui -> ui.navigate(Example6View.class));
            }
        });

        content.add(grid);
    }
}