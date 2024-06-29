package com.amazonas.frontend.view;

import com.amazonas.common.requests.notifications.NotificationRequest;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.amazonas.frontend.model.Notification;
import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route("notifications")
public class NotificationsView extends BaseLayout {
    private final AppController appController;
    private VirtualList<Notification> notificationList;
    private List<Notification> notifications;

    public NotificationsView(AppController appController) {
        super(appController);
        this.appController = appController;
        String message = appController.getNotificationsMessage();
        H2 h1 = new H2(message);
        h1.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        content.add(h1);

        // Initialize notifications list
        notifications = new ArrayList<>();

        // TODO :Get notifications from the backend (we receive error)
        NotificationRequest request = new NotificationRequest(AppController.getCurrentUserId());

        try {
            notifications = appController.postByEndpoint(Endpoints.GET_NOTIFICATIONS,request);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
        // TODO : go through the notifications and set them as read

        List<Notification> unreadNotifications = new ArrayList<>();

        try {
            unreadNotifications = appController.postByEndpoint(Endpoints.GET_UNREAD_NOTIFICATIONS,request);
        } catch (ApplicationException e) {
            openErrorDialog(e.getMessage());
        }
        for (Notification notification : unreadNotifications) {
                try {
                    appController.postByEndpoint(Endpoints.SET_READ_VALUE, new NotificationRequest(AppController.getCurrentUserId()));
                } catch (ApplicationException e) {
                    openErrorDialog(e.getMessage());
                }
        }
//        // Example notifications
//        notifications.addAll(Arrays.asList(
//                new Notification("Alice", "Welcome", "Welcome to our platform!"),
//                new Notification("Bob", "Update", "Here are the latest updates."),
//                new Notification("Charlie", "Reminder", "Don't forget about the meeting tomorrow. This is an important reminder.")
//        ));

        // Create a VirtualList to display notifications
        notificationList = new VirtualList<>();
        notificationList.setHeight("400px"); // Set a fixed height for better performance

        // Configure the VirtualList with a ComponentRenderer
        notificationList.setRenderer(new ComponentRenderer<>(notification -> {
            Div senderDiv = new Div();
            Span senderLabel = new Span("Sender: ");
            senderLabel.getStyle().set("font-weight", "bold");
            senderDiv.add(senderLabel);
            senderDiv.add(new Span(notification.sender()));

            Div titleDiv = new Div();
            Span titleLabel = new Span("Title: ");
            titleLabel.getStyle().set("font-weight", "bold");
            titleDiv.add(titleLabel);
            titleDiv.add(new Span(notification.title()));

            Button detailsButton = new Button("Details", e -> showNotificationDialog(notification));

            HorizontalLayout detailsLayout = new HorizontalLayout(detailsButton);
            detailsLayout.setWidthFull();
            detailsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

            VerticalLayout itemLayout = new VerticalLayout(senderDiv, titleDiv, detailsLayout);
            itemLayout.setPadding(true);
            itemLayout.setWidthFull();

            return itemLayout;
        }));

        // Set items to VirtualList
        notificationList.setItems(notifications);

        // Add the VirtualList to the layout
        content.add(notificationList);
    }

    private void showNotificationDialog(Notification notification) {
        Dialog dialog = new Dialog();

        Div senderDiv = new Div();
        Span senderLabel = new Span("Sender: ");
        senderLabel.getStyle().set("font-weight", "bold");
        senderDiv.add(senderLabel);
        senderDiv.add(new Span(notification.sender()));

        Div titleDiv = new Div();
        Span titleLabel = new Span("Title: ");
        titleLabel.getStyle().set("font-weight", "bold");
        titleDiv.add(titleLabel);
        titleDiv.add(new Span(notification.title()));

        Div contentDiv = new Div();
        Span contentLabel = new Span("Content: ");
        contentLabel.getStyle().set("font-weight", "bold");
        contentDiv.add(contentLabel);
        contentDiv.add(new Span(notification.content()));

        Button deleteButton = new Button("Delete", e -> {
            Dialog confirmDialog = new Dialog();
            confirmDialog.add(new Text("Are you sure you want to delete this notification?"));

            Button confirmButton = new Button("Confirm", event -> {
                // TODO :Delete the notification from the backend (we receive error)
                NotificationRequest request = new NotificationRequest(AppController.getCurrentUserId());
                try {
                    appController.postByEndpoint(Endpoints.DELETE_NOTIFICATION,request);
                } catch (ApplicationException ex) {
                    openErrorDialog(ex.getMessage());
                }
                notifications.remove(notification);
                notificationList.setItems(notifications);
                confirmDialog.close();
                dialog.close();
            });

            Button cancelButton = new Button("Cancel", event -> {
                confirmDialog.close();
            });

            HorizontalLayout buttonLayout = new HorizontalLayout(confirmButton, cancelButton);
            confirmDialog.add(buttonLayout);
            confirmDialog.open();
        });

        Button closeButton = new Button("Close", e -> dialog.close());
        HorizontalLayout buttonLayout = new HorizontalLayout(deleteButton, closeButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        dialog.add(senderDiv, titleDiv, contentDiv, buttonLayout);
        dialog.open();
    }

}
