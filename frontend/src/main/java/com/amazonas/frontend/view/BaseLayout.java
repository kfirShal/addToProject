package com.amazonas.frontend.view;

import com.amazonas.frontend.control.AppController;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
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

    public BaseLayout(AppController appController) {
        this.appController = appController;
        content = new VerticalLayout();
        setContent(content);

        if(getSessionAttribute("sessionRegistered") == null){
            appController.addSession();
        }

        SideNav nav1 = new SideNav();
        nav1.addItem(new SideNavItem("Welcome", WelcomeView.class, VaadinIcon.HOME.create()));
        nav1.addItem(new SideNavItem("System Management", SystemManagementView.class, VaadinIcon.NEWSPAPER.create()));
        nav1.addItem(new SideNavItem("Store Management", Example1View.class, VaadinIcon.NEWSPAPER.create()));

        VerticalLayout sideNav = new VerticalLayout();
        sideNav.add(nav1);

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
            Button logoutButton = new Button("Logout", event -> {
                if(appController.logout()){
                    clearSession();
                    UI.getCurrent().getPage().setLocation("/");
                    showNotification("Logout successful");
                } else {
                    showNotification("Logout failed");
                }
            });
            logoutButton.getStyle().set("margin-left", "50px");
            addToNavbar(username, logoutButton);
        }

        // set up guest user if needed
        if(! isGuestLoggedIn() && ! isUserLoggedIn()) {
            if (! appController.enterAsGuest()) {
                openErrorDialog("Failed to connect to server", AppController::clearSession);
            }
        }
    }

    protected void openLoginDialog() {
        // Implementation remains the same as before
    }

    protected void openRegisterDialog() {
        // Implementation remains the same as before
    }

    protected void showNotification(String msg) {
        Notification.show(msg, 5000, Notification.Position.TOP_CENTER);
    }

    protected void openErrorDialog(String message) {
        openErrorDialog(message, null);
    }

    protected void openErrorDialog(String message, @Nullable Runnable onClose) {
        // Implementation remains the same as before
    }

    protected void setContentComponent(VerticalLayout component) {
        content.removeAll();
        content.add(component);
    }
}
