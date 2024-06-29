package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("example3")
public class Example3View extends BaseLayout {

    private final AppController appController;

    public Example3View(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window title
        String newTitle = "Manage Store Officials";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Create and configure the grids
        // Grid for store founder (assuming only one founder, displaying the user's name)
        H3 founderTitle = new H3("Store Founder");
        founderTitle.getStyle().set("margin-top", "30px");
        Grid<String> founderGrid = new Grid<>();
        founderGrid.setItems("Your Name"); // Replace with actual user's name
        founderGrid.addColumn(name -> name).setHeader("Name");
        content.add(founderTitle, founderGrid);

        // Grid for store owners
        H3 ownersTitle = new H3("Store Owners");
        ownersTitle.getStyle().set("margin-top", "30px");
        Grid<String> ownersGrid = new Grid<>();
        List<String> owners = new ArrayList<>();
        // Populate owners list with data if available
        ownersGrid.setItems(owners);
        ownersGrid.addColumn(name -> name).setHeader("Name");
        content.add(ownersTitle, ownersGrid);

        // Add button for adding store owners
        Button addOwnerButton = new Button("Add Owner", event -> {
            // Logic to add a new owner
        });
        content.add(addOwnerButton);

        // Grid for store managers
        H3 managersTitle = new H3("Store Managers");
        managersTitle.getStyle().set("margin-top", "30px");
        Grid<String> managersGrid = new Grid<>();
        List<String> managers = new ArrayList<>();
        // Populate managers list with data if available
        managersGrid.setItems(managers);
        managersGrid.addColumn(name -> name).setHeader("Name");
        content.add(managersTitle, managersGrid);

        // Add button for adding store managers
        Button addManagerButton = new Button("Add Manager", event -> {
            // Logic to add a new manager
        });
        content.add(addManagerButton);

    }
}