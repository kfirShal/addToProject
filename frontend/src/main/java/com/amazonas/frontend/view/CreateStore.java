package com.amazonas.frontend.view;

import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.requests.stores.StoreCreationRequest;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.List;

import static com.amazonas.frontend.control.AppController.getCurrentUserId;

@Route("createstore")
public class CreateStore extends BaseLayout {
    private final AppController appController;
    private final TextField storeNameField;
    private final TextField descriptionField;

    public CreateStore(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window's title
        String newTitle = "Create Store";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);


        // set form layout
        storeNameField = new TextField("Store Name");

        descriptionField = new TextField("Description");

        FormLayout formLayout = new FormLayout();

        formLayout.add(storeNameField, descriptionField);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        formLayout.setColspan(descriptionField, 2);

        Button saveButton = new Button("Create Store", event -> createNewStore());
        formLayout.add(saveButton);
        content.add(formLayout);

    }

    private void createNewStore() {
        String storeName = storeNameField.getValue();
        String description = descriptionField.getValue();

        if (storeName.isEmpty() || description.isEmpty()) {
            showNotification("Please fill in all fields.");
            return;
        }
        try {
            StoreCreationRequest request = new StoreCreationRequest(storeName, description, getCurrentUserId());
            List<String> storeId = appController.postByEndpoint(Endpoints.ADD_STORE, request);
            showNotification("Store created successfully!");
            clearFields(); // Clear fields after successful submission
            permissionsProfile.addStorePermission(storeId.getFirst(), StoreActions.ALL);
        } catch (ApplicationException e) {
            openErrorDialog("Failed to create store: " + e.getMessage());
        }
    }

    private void clearFields() {
        storeNameField.clear();
        descriptionField.clear();
    }
}