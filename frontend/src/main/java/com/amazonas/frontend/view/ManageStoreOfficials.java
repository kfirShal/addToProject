package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("example3")
public class ManageStoreOfficials extends BaseLayout {
    private final AppController appController;
    private String storeId;

    public ManageStoreOfficials(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window's title
        String newTitle = "Manage Store Officials";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Grid for store owners
        H3 ownersTitle = new H3("Store Owners");
        ownersTitle.getStyle().set("margin-top", "30px");
        Grid<String> ownersGrid = new Grid<>();
        List<String> owners = new ArrayList<>();
        ownersGrid.setItems(owners);
        ownersGrid.addColumn(name -> name).setHeader("Name");
        ownersGrid.addColumn(name -> name).setHeader("Notifications & Respond");
        content.add(ownersTitle, ownersGrid);

        ownersGrid.addComponentColumn(product -> {
            Button removeButton = new Button("Remove", click -> {
                try {
                    appController.postByEndpoint(Endpoints.REMOVE_PRODUCT, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
            });
            return removeButton;
        });

        // Add buttons for adding and removing store owners
        Button addOwnerButton = new Button("Add", event -> {
            // Logic to add a new owner
        });

        HorizontalLayout ownersButtonsLayout = new HorizontalLayout(addOwnerButton);
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

        ownersGrid.addComponentColumn(product -> {
            Button removeButton = new Button("Remove", click -> {
                try {
                    appController.postByEndpoint(Endpoints.REMOVE_PRODUCT, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
            });
            return removeButton;
        });

        // Add button for adding store managers
        Button addManagerButton = new Button("Add", event -> {
            // Logic to add a new manager
        });
        Button editPermissionsButton = new Button("Edit Permissions", event -> {
            // Logic to edit manager permissions
        });

        HorizontalLayout managersButtonsLayout = new HorizontalLayout(addManagerButton, editPermissionsButton);
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

