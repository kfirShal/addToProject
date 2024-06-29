package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("example3")
public class Example3View extends BaseLayout {

    private final AppController appController;

    public Example3View(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window's title
        String newTitle = "Manage Store Officials";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

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
        ownersGrid.setItems(owners);
        ownersGrid.addColumn(name -> name).setHeader("Name");
        ownersGrid.addColumn(name -> name).setHeader("Notifications & Respond");
        content.add(ownersTitle, ownersGrid);

        // Add buttons for adding and removing store owners
        Button addOwnerButton = new Button("Add Owner", event -> {
            // Logic to add a new owner
        });
        Button removeOwnerButton = new Button("Remove Owner", event -> {
            // Logic to remove an owner
        });
        Button removeMyselfButton = new Button("Remove Myself", event -> {
            // Logic to remove the current user as an owner
        });

        HorizontalLayout ownersButtonsLayout = new HorizontalLayout(addOwnerButton, removeOwnerButton, removeMyselfButton);
        content.add(ownersButtonsLayout);


        // Grid for store managers
        H3 managersTitle = new H3("Store Managers");
        managersTitle.getStyle().set("margin-top", "30px");
        Grid<String> managersGrid = new Grid<>();
        List<String> managers = new ArrayList<>();
        managersGrid.setItems(managers);
        managersGrid.addColumn(name -> name).setHeader("Name");
        managersGrid.addColumn(name -> name).setHeader("Permissions");
        content.add(managersTitle, managersGrid);

        // Add button for adding store managers
        Button addManagerButton = new Button("Add Manager", event -> {
            // Logic to add a new manager
        });
        // Add buttons for removing manager, editing permissions, and permissions field
        Button removeManagerButton = new Button("Remove Manager", event -> {
            // Logic to remove a manager
        });
        Button editPermissionsButton = new Button("Edit Permissions", event -> {
            // Logic to edit manager permissions
        });

        HorizontalLayout managersButtonsLayout = new HorizontalLayout(addManagerButton, removeManagerButton, editPermissionsButton);
        content.add(managersButtonsLayout);
    }

    //TODO:
    // make components such as add manager visible to only certain people based on their permissions
    // connect to:
    // GET_STORE_ROLES_INFORMATION
    // ADD_OWNER
    // ADD_MANAGER
    // REMOVE_OWNER
    // REMOVE_MANAGER
    // ADD_PERMISSION_TO_MANAGER
    // REMOVE_PERMISSION_FROM_MANAGER

}

