package com.amazonas.frontend.view;

import com.amazonas.common.dtos.StorePosition;
import com.amazonas.common.dtos.StoreRole;
import com.amazonas.common.dtos.UserInformation;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.common.requests.stores.StorePermissionRequest;
import com.amazonas.common.requests.stores.StoreStaffRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route("managestoreofficials")
public class ManageStoreOfficials extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;
    private String storeId;
    private String userId;
    private Dialog addOwnerDialog;
    private Dialog addManagerDialog;
    private final Grid<UserInformation> ownersGrid = new Grid<>();
    private final Grid<UserInformation> managersGrid = new Grid<>();

    public ManageStoreOfficials(AppController appController)  {
        super(appController);
        this.appController = appController;
    }

    private void createView() {
        storeId = getParam("storeid");
        // Set the window's title
        String newTitle = "Manage Store Officials";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);

        // Store Owners section
        H3 ownersTitle = new H3("Store Owners");
        ownersTitle.getStyle().set("margin-top", "30px");
        ownersGrid.addColumn(UserInformation::getUserId).setHeader("ID");
        ownersGrid.addColumn(UserInformation::getEmail).setHeader("Email");

        ownersGrid.addComponentColumn(user -> new Button("Remove Owner", _ -> {
            try {
                StoreStaffRequest request = new StoreStaffRequest(storeId, userId, user.getUserId());
                appController.postByEndpoint(Endpoints.REMOVE_OWNER, request);
                refreshGrid();
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
        }));

        addOwnerDialog = createUserDialog("Add Owner", this::addOwner);

        Button addOwnerButton = new Button("Add", click -> addOwnerDialog.open());
        HorizontalLayout ownersButtonsLayout = new HorizontalLayout(addOwnerButton);

        // Store Managers section
        H3 managersTitle = new H3("Store Managers");
        managersTitle.getStyle().set("margin-top", "30px");
        managersGrid.addColumn(UserInformation::getUserId).setHeader("ID");
        managersGrid.addColumn(UserInformation::getEmail).setHeader("Email");

        managersGrid.addComponentColumn(user -> {
            MultiSelectComboBox<String> permissionsComboBox = new MultiSelectComboBox<>();
            permissionsComboBox.setItems(fetchAvailablePermissions().stream().map(Enum::name).collect(Collectors.toList()));
            try {
                List<PermissionsProfile> prmissions = appController.postByEndpoint(Endpoints.GET_USER_PERMISSIONS, user.getUserId());
                permissionsComboBox.setValue(prmissions.getFirst().getStorePermissions(storeId).stream().map(StoreActions::toString).toList());
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
            }
            permissionsComboBox.addValueChangeListener(event -> {
                Set<String> addedPermissions = event.getValue();
                Set<String> removedPermissions = event.getOldValue();
                removedPermissions.removeAll(addedPermissions);

                addedPermissions.forEach(permission -> {
                    try {
                        StorePermissionRequest permissionRequest = new StorePermissionRequest(storeId, user.getUserId(), StoreActions.ADD_PERMISSION_TO_MANAGER.toString());
                        appController.postByEndpoint(Endpoints.ADD_PERMISSION_TO_MANAGER, permissionRequest);
                    } catch (ApplicationException e) {
                        openErrorDialog(e.getMessage());
                    }
                });

                removedPermissions.forEach(permission -> {
                    try {
                        StorePermissionRequest permissionRequest = new StorePermissionRequest(storeId, user.getUserId(), StoreActions.REMOVE_PERMISSION_FROM_MANAGER.toString());
                        appController.postByEndpoint(Endpoints.REMOVE_PERMISSION_FROM_MANAGER, permissionRequest);
                        refreshGrid();
                    } catch (ApplicationException e) {
                        openErrorDialog(e.getMessage());
                    }
                });
            });

            return permissionsComboBox;
        }).setHeader("Permissions");

        managersGrid.addComponentColumn(user  -> {
            Button removeButton = new Button("Remove Manager", click -> {
                try {
                    StoreStaffRequest request = new StoreStaffRequest(storeId, userId, user.getUserId());
                    appController.postByEndpoint(Endpoints.REMOVE_MANAGER, request);
                    refreshGrid();
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
            });
            return removeButton;
        });

        addManagerDialog = createUserDialog("Add Manager", this::addManager);
        Button addManagerButton = new Button("Add", click -> addManagerDialog.open());
        HorizontalLayout managersButtonsLayout = new HorizontalLayout(addManagerButton);

        content.add(ownersTitle, ownersGrid);
        content.add(addOwnerDialog);
        content.add(ownersButtonsLayout);
        content.add(managersTitle, managersGrid);
        content.add(addManagerDialog);
        content.add(managersButtonsLayout);
        refreshGrid();
    }

    private Dialog createUserDialog(String dialogTitle, Runnable saveAction) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H2 title = new H2(dialogTitle);
        dialog.add(title);

        FormLayout formLayout = new FormLayout();
        TextField userIdField = new TextField("User ID");
        formLayout.add(userIdField);

        Button saveButton = new Button("Save Changes", e -> {
            saveAction.run();
            dialog.close();
        });
        Button discardButton = new Button("Discard", e -> dialog.close());
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, discardButton);
        dialog.add(formLayout, buttonsLayout);
        return dialog;
    }

    private void addOwner() {
        TextField userIdField = (TextField) addOwnerDialog.getChildren().filter(component -> component instanceof TextField).findFirst().get();
        String addedUserId = userIdField.getValue();
        StoreStaffRequest request = new StoreStaffRequest(storeId, userId, addedUserId);
        try {
            appController.postByEndpoint(Endpoints.ADD_OWNER, request);
            refreshGrid();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private void addManager() {
        TextField userIdField = (TextField) addOwnerDialog.getChildren().filter(component -> component instanceof TextField).findFirst().get();
        String addedUserId = userIdField.getValue();
        StoreStaffRequest request = new StoreStaffRequest(storeId, userId, addedUserId);
        try {
            appController.postByEndpoint(Endpoints.ADD_MANAGER, request);
            refreshGrid();
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
    }

    private void refreshGrid() {
        List<UserInformation> owners = new ArrayList<>();
        List<UserInformation> managers = new ArrayList<>();

        List<StorePosition> roles = null;
        try {
            roles = appController.postByEndpoint(Endpoints.GET_STORE_ROLES_INFORMATION, storeId);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
            return;
        }

        for (StorePosition idToRole : roles) {
            try {
                List<UserInformation> userInfomation = appController.postByEndpoint(Endpoints.GET_USER_INFORMATION, idToRole.userId());
                if (idToRole.role() == StoreRole.STORE_OWNER) {
                    owners.add(userInfomation.get(0));

                } else if (idToRole.role() == StoreRole.STORE_MANAGER) {
                    managers.add(userInfomation.get(0));
                }
            } catch (ApplicationException e) {
                openErrorDialog(e.getMessage());
                return;
            }
        }

        ownersGrid.setItems(owners);
        managersGrid.setItems(managers);
    }

    private Set<StoreActions> fetchAvailablePermissions() {
        return Set.of(StoreActions.values());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }

}