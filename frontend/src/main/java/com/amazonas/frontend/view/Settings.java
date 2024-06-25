package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;

@Route("Settings")
public class Settings extends Profile {
    private AppController appController;

    public Settings(AppController appController) {
        super(appController);
        this.appController = appController;
        returnToMainIfNotLogged();
        createSettingsLayout();
    }

    private void createSettingsLayout() {
        VerticalLayout settingsLayout = new VerticalLayout();

        // Personal Information Section
        FormLayout personalInfoForm = new FormLayout();
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        EmailField email = new EmailField("Email");
        Button updatePersonalInfoButton = new Button("Update Personal Information", event -> {
            // Add logic to update personal information
            Notification.show("Personal information updated");
        });
        personalInfoForm.add(firstName, lastName, email, updatePersonalInfoButton);

        // Password Change Section
        FormLayout passwordChangeForm = new FormLayout();
        PasswordField currentPassword = new PasswordField("Current Password");
        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmNewPassword = new PasswordField("Confirm New Password");
        Button changePasswordButton = new Button("Change Password", event -> {
            // Add logic to change password
            if (newPassword.getValue().equals(confirmNewPassword.getValue())) {
                Notification.show("Password changed successfully");
            } else {
                Notification.show("Passwords do not match");
            }
        });
        passwordChangeForm.add(currentPassword, newPassword, confirmNewPassword, changePasswordButton);

        // Address Management Section
        FormLayout addressForm = new FormLayout();
        TextField addressLine1 = new TextField("Address Line 1");
        TextField addressLine2 = new TextField("Address Line 2");
        TextField city = new TextField("City");
        TextField state = new TextField("State");
        TextField zipCode = new TextField("Zip Code");
        Button updateAddressButton = new Button("Update Address", event -> {
            // Add logic to update address
            Notification.show("Address updated");
        });
        addressForm.add(addressLine1, addressLine2, city, state, zipCode, updateAddressButton);

        // Notification Preferences Section
        FormLayout notificationPreferencesForm = new FormLayout();
        // Add fields for managing notification preferences here
        // Example: CheckBox emailNotifications = new CheckBox("Email Notifications");
        // Button updateNotificationPreferencesButton = new Button("Update Notification Preferences", event -> {
        //     // Add logic to update notification preferences
        //     Notification.show("Notification preferences updated");
        // });
        // notificationPreferencesForm.add(emailNotifications, updateNotificationPreferencesButton);

        settingsLayout.add(personalInfoForm, passwordChangeForm, addressForm /*, notificationPreferencesForm */);
        content.add(settingsLayout);
    }
}
