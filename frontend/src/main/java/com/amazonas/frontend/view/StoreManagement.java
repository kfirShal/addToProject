package com.amazonas.frontend.view;

import com.amazonas.common.dtos.StorePosition;
import com.amazonas.common.dtos.StoreRole;
import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.utils.Pair;
import com.amazonas.frontend.control.AppController;
import com.amazonas.frontend.control.Endpoints;
import com.amazonas.frontend.exceptions.ApplicationException;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import java.util.Arrays;
import java.util.List;

@Route("storemanagement")
public class StoreManagement extends BaseLayout implements BeforeEnterObserver {

    public static final String MANAGE_INVENTORY = "Manage Inventory";
    public static final String MANAGE_STORE_OFFICIALS = "Manage Store Officials";
    public static final String PURCHASE_POLICY = "Purchase Policy";
    public static final String DISCOUNT_POLICY = "Discount Policy";
    public static final String VIEW_PURCHASE_HISTORY = "View Purchase History";
    public static final String CLOSE_REOPEN_STORE = "Close & Reopen Store";
    private final AppController appController;

    public StoreManagement(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    private void createView() {
        String storeId = getParam("storeid");

        // Set the window title
        String newTitle = "Store Management";
        H2 title = new H2(newTitle);
        title.getStyle().set("align-self", "center");
        content.add(title);

        // Create a grid with 5 items
        Grid<String> grid = new Grid<>();
        List<String> items = Arrays.asList(MANAGE_INVENTORY, MANAGE_STORE_OFFICIALS, PURCHASE_POLICY, DISCOUNT_POLICY, VIEW_PURCHASE_HISTORY, CLOSE_REOPEN_STORE);

        grid.setItems(items);
        grid.addColumn(item -> item).setHeader("Operations");

        grid.addItemClickListener(event -> {
            String item = event.getItem();
            // Navigate to a new view based on the item clicked
            if (MANAGE_INVENTORY.equals(item)) {
                String url = getPath("manageinventory", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
            if (MANAGE_STORE_OFFICIALS.equals(item)) {
                if (permissionsProfile.hasPermission(storeId, StoreActions.VIEW_ROLES_INFORMATION)) {
                    String url = getPath("managestoreofficials", Pair.of("storeid", storeId));
                    getUI().ifPresent(ui -> ui.navigate(url));
                } else {
                    Notification.show("You do not have permission to view store officials");
                }
            }
            if (PURCHASE_POLICY.equals(item)) {
                String url = getPath("purchasepolicy", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
            if (DISCOUNT_POLICY.equals(item)) {
                String url = getPath("discountpolicy", Pair.of("storeid", storeId));
                getUI().ifPresent(ui -> ui.navigate(url));
            }
            if (VIEW_PURCHASE_HISTORY.equals(item)) {
                if (permissionsProfile.hasPermission(storeId, StoreActions.VIEW_STORE_TRANSACTIONS) || permissionsProfile.hasPermission(MarketActions.ALL)) {
                    String url = getPath("purchasehistory", Pair.of("storeid", storeId));
                    getUI().ifPresent(ui -> ui.navigate(url));
                } else {
                    Notification.show("You do not have permission to view purchase history.");
                }
            }

            if (CLOSE_REOPEN_STORE.equals(item)) {
//                List<StorePosition> positions;
//                try {
//                    positions = appController.postByEndpoint(Endpoints.GET_STORE_ROLES_INFORMATION,storeId);
//                } catch (ApplicationException e) {
//                    openErrorDialog(e.getMessage());
//                    return;
//                }
//
//                boolean isFounder = positions.stream()
//                        .filter(p -> p.role() == StoreRole.STORE_FOUNDER)
//                        .toList()
//                        .getFirst()
//                        .userId()
//                        .equalsIgnoreCase(AppController.getCurrentUserId());
//
//                boolean isSystemManager = permissionsProfile.hasPermission();
//
//
//                if(isFounder || isSystemManager){
                    String url = getPath("closeandreopen", Pair.of("storeid", storeId));
                    getUI().ifPresent(ui -> ui.navigate(url));
//                } else {
//                    Notification.show("You do not have permissions to close and open the store!!");
//                }
            }
        });

        content.add(grid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        params = beforeEnterEvent.getLocation().getQueryParameters();
        createView();
    }
}
