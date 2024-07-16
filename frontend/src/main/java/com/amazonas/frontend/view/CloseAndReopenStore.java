package com.amazonas.frontend.view;

import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.amazonas.common.permissions.actions.StoreActions;


@Route("closeandreopen")
public class CloseAndReopenStore extends BaseLayout implements BeforeEnterObserver {
    private final AppController appController;
    private String storeId = getParam("storeid");

    public CloseAndReopenStore(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    private void createView(){
        // Set the window title
        String newTitle = "Close/Reopen Store";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);

        // Close store button
        Button closeStoreButton = new Button("Close Store");
        closeStoreButton.addClickListener(event -> showCloseStoreConfirmation());
        VerticalLayout closeStoreLayout = new VerticalLayout();
        closeStoreLayout.add(closeStoreButton, new Paragraph("When a store closes it becomes inactve.\n" +
                "Users who are not store owners or system administrators cannot receive information\n" +
                "about a closed (inactive) store. Owners and managers of a store that closes receive a notification of its closure,\n" +
                "and their appointment is not affected.\n" +
                "The products of an inactive store do not appear in the results of a product information requests\n" +
                "in the market.\n "));
        content.add(closeStoreLayout);
        closeStoreButton.getStyle().set("background-color", "red");
        closeStoreButton.getStyle().set("color", "white");

        // Reopen store button
        Button reopenStoreButton = new Button("Reopen Store");
        reopenStoreButton.addClickListener(event -> reopenStore());
        VerticalLayout reopenStoreLayout = new VerticalLayout();
        reopenStoreLayout.add(reopenStoreButton, new Paragraph("Reopening a store that was previously closed will lead to sending a \n" +
                "notification to the owners and managers of the store"));
        content.add(reopenStoreLayout);
        reopenStoreButton.getStyle().set("background-color", "green");
        reopenStoreButton.getStyle().set("color", "white");
    }

    private void showCloseStoreConfirmation() {
        Dialog confirmationDialog = new Dialog();
        confirmationDialog.setCloseOnEsc(false);
        confirmationDialog.setCloseOnOutsideClick(false);

        // Dialog content
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.add(new H2("Confirm Store Closure"),
                new H2("Are you sure you want to close the store?"));

        // Buttons inside the dialog
        Button confirmButton = new Button("Confirm", event -> {
            closeStore();
            confirmationDialog.close();
        });
        Button cancelButton = new Button("Cancel", event -> confirmationDialog.close());
        dialogLayout.add(confirmButton, cancelButton);

        confirmationDialog.add(dialogLayout);
        confirmationDialog.open();
    }

    private void closeStore() {
        if (permissionsProfile.hasPermission(storeId, StoreActions.CLOSE_STORE) || permissionsProfile.hasPermission(MarketActions.ALL)) {
            try {
                appController.postByEndpoint(Endpoints.CLOSE_STORE, storeId);
                Notification.show("Store is closed.");
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        } else {
            Notification.show("You do not have permission to close the store.");
        }
    }

    private void reopenStore() {
        if (permissionsProfile.hasPermission(storeId, StoreActions.OPEN_STORE)) {
            try {
                appController.postByEndpoint(Endpoints.OPEN_STORE, storeId);
                Notification.show("Store is reopened.");
            } catch (ApplicationException e) {
                throw new RuntimeException(e);
            }
        } else {
            Notification.show("You do not have permission to reopen the store.");
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }
}
