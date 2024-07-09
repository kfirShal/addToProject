package com.amazonas.frontend.view;

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

@Route("openstore")
public class OpenStore extends BaseLayout {
    private final AppController appController;
    private final TextField userIdField;
    private final TextField storeNameField;
    private final TextField descriptionField;

    public OpenStore(AppController appController) {
        super(appController);
        this.appController = appController;

        // Set the window's title
        String newTitle = "Open Store";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);


        // set form layout
        userIdField = new TextField("User ID");

        storeNameField = new TextField("Store Name");

        descriptionField = new TextField("Description");

        FormLayout formLayout = new FormLayout();

        formLayout.add(userIdField, storeNameField, descriptionField);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2));

        formLayout.setColspan(descriptionField, 2);

        Button saveButton = new Button("Open Store", event -> saveStore());
        formLayout.add(saveButton);
        content.add(formLayout);

    }

    private void saveStore() {
        String userId = userIdField.getValue();
        String storeName = storeNameField.getValue();
        String description = descriptionField.getValue();

        if (userId.isEmpty() || storeName.isEmpty() || description.isEmpty()) {
            Notification.show("Please fill in all fields.");
            return;
        }
        try {
            StoreCreationRequest request = new StoreCreationRequest(userId, storeName, description);
            appController.postByEndpoint(Endpoints.ADD_STORE, request);

            Notification.show("Store opened successfully!");
            clearFields(); // Clear fields after successful submission
        } catch (ApplicationException e) {
            openErrorDialog("Failed to open store: " + e.getMessage());
        }
    }

    private void clearFields() {
        userIdField.clear();        // Clear userId field
        storeNameField.clear();     // Clear storeName field
        descriptionField.clear();   // Clear description field
    }
}
