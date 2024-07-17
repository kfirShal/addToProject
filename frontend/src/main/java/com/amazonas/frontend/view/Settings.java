package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.checkbox.Checkbox;


@Route("Settings")
public class Settings extends Profile {

    public Settings(AppController appController) {
        super(appController);
        returnToMainIfNotLogged();
        createSettingsLayout();
    }

    private void createSettingsLayout() {
        VerticalLayout settingsLayout = new VerticalLayout();

        // Create an Accordion
        Accordion accordion = new Accordion();

        // Personal Information Section
        FormLayout personalInfoForm = getPersonalInfoForm();
        accordion.add("Personal Information", personalInfoForm);

        // Password Change Section
        FormLayout passwordChangeForm = getPasswordChangeForm();
        accordion.add("Change Password", passwordChangeForm);

        // Address Management Section
        FormLayout addressForm = getAddressForm();
        accordion.add("Address Management", addressForm);

        // Notification Preferences Section
        FormLayout notificationPreferencesForm = getNotificationPreferencesForm();
        accordion.add("Notification Preferences", notificationPreferencesForm);

        settingsLayout.add(accordion);
        content.add(settingsLayout);
    }

    private FormLayout getNotificationPreferencesForm() {
        FormLayout notificationPreferencesForm = new FormLayout();
        // Example: Add fields for managing notification preferences here
        Checkbox emailNotifications = new Checkbox("Email Notifications");
        Button updateNotificationPreferencesButton = new Button("Update Notification Preferences", _ -> {
             // Add logic to update notification preferences
             showNotification("Notification preferences updated");
        });
        notificationPreferencesForm.add(emailNotifications, updateNotificationPreferencesButton);
        return notificationPreferencesForm;
    }

    private FormLayout getPersonalInfoForm() {
        FormLayout personalInfoForm = new FormLayout();
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        EmailField email = new EmailField("Email");
        Button updatePersonalInfoButton = new Button("Update Personal Information", _ -> {
            // Add logic to update personal information
            showNotification("Personal information updated");
        });
        personalInfoForm.add(firstName, lastName, email, updatePersonalInfoButton);
        return personalInfoForm;
    }

    private FormLayout getPasswordChangeForm() {
        FormLayout passwordChangeForm = new FormLayout();
        PasswordField currentPassword = new PasswordField("Current Password");
        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmNewPassword = new PasswordField("Confirm New Password");
        Button changePasswordButton = new Button("Change Password", _ -> {
            // Add logic to change password
            if (newPassword.getValue().equals(confirmNewPassword.getValue())) {
                showNotification("Password changed successfully");
            } else {
                showNotification("Passwords do not match");
            }
        });
        passwordChangeForm.add(currentPassword, newPassword, confirmNewPassword, changePasswordButton);
        return passwordChangeForm;
    }

    private FormLayout getAddressForm() {
        FormLayout addressForm = new FormLayout();
        TextField addressLine1 = new TextField("Address Line 1");
        TextField addressLine2 = new TextField("Address Line 2");
        TextField city = new TextField("City");
        TextField state = new TextField("State");
        TextField zipCode = new TextField("Zip Code");
        Button updateAddressButton = new Button("Update Address", _ -> {
            // Add logic to update address
            showNotification("Address updated");
        });
        addressForm.add(addressLine1, addressLine2, city, state, zipCode, updateAddressButton);
        return addressForm;
    }
}
