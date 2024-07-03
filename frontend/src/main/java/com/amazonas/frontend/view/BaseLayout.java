package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import static com.amazonas.frontend.control.AppController.*;

@PageTitle("Amazonas")
@Component
public abstract class BaseLayout extends AppLayout {

    protected final VerticalLayout content;
    private final AppController appController;
    protected SideNav nav1;
    protected SideNav nav2;
    protected String user;

    public BaseLayout(AppController appController) {
        this.appController = appController;
        content = new VerticalLayout();
        setContent(content);

        if(getSessionAttribute("sessionRegistered") == null){
            appController.addSession();
        }

        nav1 = new SideNav();
        nav1.addItem(new SideNavItem("Welcome", WelcomeView.class, VaadinIcon.HOME.create()));
        nav1.addItem(new SideNavItem("Products", ProductsView.class, VaadinIcon.CART.create()));
        nav1.addItem(new SideNavItem("Categories", CategoriesView.class, VaadinIcon.TAGS.create()));

        nav2 = new SideNav();
        nav2.setLabel("------------------");
        nav2.addItem(new SideNavItem("Profile", Profile.class, VaadinIcon.USER.create()));
        nav2.addItem(new SideNavItem("Settings", Settings.class, VaadinIcon.COG.create()));


        VerticalLayout sideNav = new VerticalLayout();
        sideNav.add(nav1, nav2);

        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Amazonas");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Scroller scroller = new Scroller(sideNav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);
        addToNavbar(toggle, title);

        // set up login/logout button
        if (! isUserLoggedIn()) {
            Button loginButton = new Button("Login", event -> openLoginDialog());
            Button registerButton = new Button("Register", event -> openRegisterDialog());
            loginButton.getStyle().setMarginLeft("75%");
            registerButton.getStyle().set("margin-left", "10px");
            addToNavbar(loginButton, registerButton);
        } else {
            H4 username = new H4("Hello, " + getCurrentUserId());
            username.getStyle().set("margin-left", "65%");

            // Profile button with an icon and text "Profile", click on it should open the page with user profile
            Button profileButton = new Button("Profile", new Icon(VaadinIcon.USER), event -> {
                UI.getCurrent().navigate("Profile");
            });
            profileButton.getStyle().set("margin-left", "10px");

            Button logoutButton = new Button("Logout", event -> {
                if(appController.logout()){
                    clearSession();
                    UI.getCurrent().getPage().setLocation("/");
                    showNotification("Logout successful");
                } else {
                    showNotification("Logout failed");
                }
            });
            logoutButton.setIcon(new Icon(VaadinIcon.SIGN_OUT));
            logoutButton.getStyle().set("margin-left", "10px");

            addToNavbar(username, profileButton, logoutButton);
        }

        // set up guest user if needed
        if(! isGuestLoggedIn() && ! isUserLoggedIn()) {
            if (! appController.enterAsGuest()) {
                appController.enterAsGuest();
                //openErrorDialog("Failed to connect to server", AppController::clearSession);
            }
        }
    }

    public void returnToMainIfNotLogged(){
//        if (!isUserLoggedIn()) {
//            UI.getCurrent().navigate("");
//            return;
//        }
    }

    protected void openLoginDialog() {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        FormLayout formLayout = new FormLayout();
        TextField usernameField = new TextField("Username");
        usernameField.setPlaceholder("Username");
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setPlaceholder("Password");

        H4 headline = new H4("Login");
        VerticalLayout headlineLayout = new VerticalLayout(headline);
        headline.getStyle().setAlignSelf(Style.AlignSelf.CENTER);

        Button submitButton = new Button("Submit", event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            if (appController.login(username, password)) {
                this.user = username;
                showNotification("Login successful");
                UI.getCurrent().getPage().reload();
            } else {
                showNotification("Login failed");
            }
        });
        submitButton.addClickShortcut(Key.ENTER);
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        formLayout.add(headlineLayout,usernameField, passwordField, submitButton, cancelButton);
        formLayout.setWidth("80%");
        formLayout.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        layout.add(formLayout);
        dialog.setWidth("30%");
        dialog.add(layout);

        content.add(dialog);
        dialog.open();

    }

    public String getName() {
        return user;
    }

    protected void openRegisterDialog(){
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        FormLayout formLayout = new FormLayout();
        EmailField emailField = new EmailField("Email");
        emailField.setPlaceholder("Email");
        TextField usernameField = new TextField("Username");
        usernameField.setPlaceholder("Username");
        PasswordField passwordField = new PasswordField("Password");
        passwordField.setPlaceholder("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");
        confirmPasswordField.setPlaceholder("Confirm Password");
        Icon confirmErrorIcon = VaadinIcon.EXCLAMATION_CIRCLE_O.create();
        confirmErrorIcon.getStyle().setMarginLeft("50px");
        confirmErrorIcon.setVisible(false);
        confirmErrorIcon.setColor("red");
        confirmErrorIcon.setTooltipText("Passwords do not match");
        confirmPasswordField.addValueChangeListener(event -> confirmErrorIcon.setVisible(false));
        passwordField.addValueChangeListener(event -> confirmErrorIcon.setVisible(false));
        confirmPasswordField.setSuffixComponent(confirmErrorIcon);

        H4 headline = new H4("Register");
        VerticalLayout headlineLayout = new VerticalLayout(headline);
        headline.getStyle().setAlignSelf(Style.AlignSelf.CENTER);

        Button submitButton = new Button("Submit", event -> {
            String email = emailField.getValue();
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();
            if(!password.equals(confirmPassword)){
                showNotification("Passwords do not match");
                confirmErrorIcon.setVisible(true);
                return;
            }
            if (appController.register(email, username, password, confirmPassword)) {
                if(appController.login(username, password)){
                    showNotification("Registration successful");
                    UI.getCurrent().getPage().reload();
                } else {
                    openErrorDialog("could not log in after registration, please try logging in manually.");
                }
            } else {
                showNotification("Registration failed");
            }
        });
        submitButton.addClickShortcut(Key.ENTER);
        Button cancelButton = new Button("Cancel", event -> dialog.close());

        formLayout.add(headlineLayout,emailField,usernameField, passwordField, confirmPasswordField, submitButton, cancelButton);
        formLayout.setWidth("80%");
        formLayout.getStyle().setAlignSelf(Style.AlignSelf.CENTER);
        layout.add(formLayout);
        dialog.add(layout);
        dialog.setWidth("30%");

        content.add(dialog);
        dialog.open();
    }

    protected void showNotification(String msg) {
        Notification.show(msg,5000, Notification.Position.TOP_CENTER);
    }

    protected void openErrorDialog(String message) {
        openErrorDialog(message, null);
    }

    /**
     * @param message Error message to display
     * @param onClose Runnable to run when the dialog is closed. if null is passed, nothing will happen
     */
    protected void openErrorDialog(String message, @Nullable Runnable onClose) {
        Dialog dialog = new Dialog();
        VerticalLayout layout = new VerticalLayout();
        H1 h1 = new H1("Error");
        H4 h4 = new H4(message);
        Button closeButton = new Button("Close", event -> dialog.close());
        layout.add(h1, h4, closeButton);
        dialog.add(layout);
        if(onClose != null){
            dialog.addOpenedChangeListener(event -> {
                if (!event.isOpened()) {
                    onClose.run();
                }
            });
        }
        content.add(dialog);
        dialog.open();
    }
}
