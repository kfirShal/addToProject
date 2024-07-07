package com.amazonas.frontend.view;

import com.amazonas.common.dtos.RegisteredUser;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.utils.POJOBinder;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route("example3")
public class ManageStoreOfficials extends BaseLayout {
    private final AppController appController;
    private String storeId;
    private final Dialog addOwnerDialog;
    private final Dialog addManagerDialog;
    private final POJOBinder<RegisteredUser> binder;
    private RegisteredUser currentRegisteredUser;
    private final Grid<RegisteredUser> ownersGrid = new Grid<>(RegisteredUser.class, false);
    private final Grid<RegisteredUser> managersGrid = new Grid<>(RegisteredUser.class, false);

    public ManageStoreOfficials(AppController appController) {
        super(appController);
        this.appController = appController;
        binder = new POJOBinder<>(RegisteredUser.class);

        // Set the window's title
        String newTitle = "Manage Store Officials";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title); // Use content from BaseLayout

        // Store Owners section
        H3 ownersTitle = new H3("Store Owners");
        ownersTitle.getStyle().set("margin-top", "30px");
        List<RegisteredUser> owners = new ArrayList<>();
        ownersGrid.setItems(owners);
        ownersGrid.addColumn(RegisteredUser::getEmail).setHeader("Email");
        content.add(ownersTitle, ownersGrid);

        ownersGrid.addComponentColumn(registeredUser -> {
            Button removeButton = new Button("Remove Owner", click -> {
                try {
                    appController.postByEndpoint(Endpoints.REMOVE_OWNER, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
            });
            return removeButton;
        });

        // Create and configure the add dialog
        addOwnerDialog = createUserDialog("Add Owner", this::addOwner, "New Owner's Email");
        content.add(addOwnerDialog);

        Button addOwnerButton = new Button("Add", click -> openAddDialog(addOwnerDialog));
        HorizontalLayout ownersButtonsLayout = new HorizontalLayout(addOwnerButton);
        content.add(ownersButtonsLayout);

        // Store Managers section
        H3 managersTitle = new H3("Store Managers");
        managersTitle.getStyle().set("margin-top", "30px");
        List<RegisteredUser> managers = new ArrayList<>();
        managersGrid.setItems(managers);
        managersGrid.addColumn(RegisteredUser::getEmail).setHeader("Email");

        // Add Multi-Select Combo Box for Permissions
        managersGrid.addComponentColumn(registeredUser -> {
            MultiSelectComboBox<String> permissionsComboBox = new MultiSelectComboBox<>();
            permissionsComboBox.setLabel("Permissions");
            permissionsComboBox.setItems(fetchAvailablePermissions().stream().map(Enum::name).collect(Collectors.toList()));

            permissionsComboBox.addValueChangeListener(event -> {
                Set<String> addedPermissions = event.getValue();
                Set<String> removedPermissions = event.getOldValue();
                removedPermissions.removeAll(addedPermissions);

                // Add new permissions
                addedPermissions.forEach(permission -> {
                    try {
                        appController.postByEndpoint(Endpoints.ADD_PERMISSION_TO_MANAGER, storeId);
                    } catch (ApplicationException e) {
                        openErrorDialog(e.getMessage());
                    }
                });

                // Remove old permissions
                removedPermissions.forEach(permission -> {
                    try {
                        appController.postByEndpoint(Endpoints.REMOVE_PERMISSION_FROM_MANAGER, storeId);
                    } catch (ApplicationException e) {
                        openErrorDialog(e.getMessage());
                    }
                });
            });

            return permissionsComboBox;
        });

        content.add(managersTitle, managersGrid);

        managersGrid.addComponentColumn(registeredUser -> {
            Button removeButton = new Button("Remove Manager", click -> {
                try {
                    appController.postByEndpoint(Endpoints.REMOVE_MANAGER, storeId);
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
            });
            return removeButton;
        });

        // Create and configure the add dialog
        addManagerDialog = createUserDialog("Add Manager", this::addManager, "New Manager's Email");
        content.add(addManagerDialog);

        Button addManagerButton = new Button("Add", click -> openAddDialog(addManagerDialog));
        HorizontalLayout managersButtonsLayout = new HorizontalLayout(addManagerButton);
        content.add(managersButtonsLayout);

        // Initial grid refresh with sample users
        refreshGrid();
    }

    private Dialog createUserDialog(String dialogTitle, Runnable saveAction, String data) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        // Add title to the dialog
        H2 title = new H2(dialogTitle);
        dialog.add(title);

        FormLayout formLayout = new FormLayout();
        TextField userEmailField = new TextField(data);
        binder.bind(userEmailField, "email");
        formLayout.add(userEmailField);

        Button saveButton = new Button("Save Changes", e -> saveAction.run());
        Button discardButton = new Button("Discard", e -> dialog.close());
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, discardButton);
        dialog.add(formLayout, buttonsLayout);
        return dialog;
    }

    private void openAddDialog(Dialog dialogType) {
        currentRegisteredUser = new RegisteredUser("-1", "");
        binder.readObject(currentRegisteredUser);
        dialogType.open();
    }


    private void addOwner() {
        binder.writeObject(currentRegisteredUser);
        try {
            appController.postByEndpoint(Endpoints.ADD_OWNER, currentRegisteredUser);
            refreshGrid();
            addOwnerDialog.close();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private void addManager() {
        binder.writeObject(currentRegisteredUser);
        try {
            appController.postByEndpoint(Endpoints.ADD_MANAGER, currentRegisteredUser);
            refreshGrid();
            addManagerDialog.close();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private void refreshGrid() {
        List<RegisteredUser> registeredUsers = getUsers();
        if (registeredUsers == null || registeredUsers.isEmpty()) {
            registeredUsers = addSampleUsers();
        }

        // Separate the users into owners and managers
        List<RegisteredUser> owners = new ArrayList<>();
        List<RegisteredUser> managers = new ArrayList<>();

        for (int i = 0; i < registeredUsers.size(); i++) {
            if (i < 2) {
                owners.add(registeredUsers.get(i));
            } else {
                managers.add(registeredUsers.get(i));
            }
        }

        ownersGrid.setItems(owners);
        managersGrid.setItems(managers);
    }

    private List<RegisteredUser> getUsers() {
        List<RegisteredUser> users = null;
        try {
            users = appController.postByEndpoint(Endpoints.GET_STORE_ROLES_INFORMATION, storeId); //what should be the payload
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
        return users;
    }

    private Set<StoreActions> fetchAvailablePermissions() {
        return Set.of(StoreActions.values());
    }

    private List<RegisteredUser> addSampleUsers() {
        List<RegisteredUser> sampleUsers = new ArrayList<>();
        sampleUsers.add(new RegisteredUser("1", "owner1@example.com"));
        sampleUsers.add(new RegisteredUser("2", "owner2@example.com"));
        sampleUsers.add(new RegisteredUser("3", "manager1@example.com"));
        sampleUsers.add(new RegisteredUser("4", "manager2@example.com"));
        return sampleUsers;
    }

}
