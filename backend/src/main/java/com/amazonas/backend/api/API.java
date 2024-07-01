package com.amazonas.backend.api;

import com.amazonas.backend.service.*;
import org.springframework.web.bind.annotation.*;

@SuppressWarnings("SpellCheckingInspection")
@RestController
public class API {

    private final AuthenticationService authenticationService;
    private final ExternalServicesService externalServicesService;
    private final MarketService marketService;
    private final NotificationsService notificationsService;
    private final StoresService storesService;
    private final UserProfilesService userProfilesService;
    private final PermissionsService permissionsService;

    public API(AuthenticationService authenticationService,
               ExternalServicesService externalServicesService,
               MarketService marketService,
               NotificationsService notificationsService,
               StoresService storesService,
               UserProfilesService userProfilesService, PermissionsService permissionsService){
        this.authenticationService = authenticationService;
        this.externalServicesService = externalServicesService;
        this.marketService = marketService;
        this.notificationsService = notificationsService;
        this.storesService = storesService;
        this.userProfilesService = userProfilesService;
        this.permissionsService = permissionsService;
    }

    @GetMapping("userprofiles/enterasguest")
    public String forwardGet() {
        return forwardUserProfiles("enterasguest", "");
    }

    @PostMapping("{service}/{endpoint}")
    public String forwardPost(@PathVariable String service,
                              @PathVariable String endpoint,
                              @RequestBody String body) {
        service=service.toLowerCase();
        endpoint=endpoint.toLowerCase();
        return switch(service){
            case "auth" -> forwardAuth(endpoint, body);
            case "external" -> forwardExternal(endpoint,body);
            case "market" -> forwardMarket(endpoint,body);
            case "notifications" -> forwardNotifications(endpoint,body);
            case "stores" -> forwardStores(endpoint,body);
            case "userprofiles" -> forwardUserProfiles(endpoint,body);
            case "permissions" -> forwardPermissions(endpoint,body);
            default -> "Invalid service";
        };
    }

    private String forwardPermissions(String endpoint, String body) {
        return switch (endpoint) {
            case "getuserpermissions" -> permissionsService.getUserPermissions(body);
            case "getguestpermissions" -> permissionsService.getGuestPermissions(body);
            default -> "Invalid endpoint";
        };
    }

    private String forwardUserProfiles(String endpoint, String body) {
        return switch (endpoint) {
            case "enterasguest" -> userProfilesService.enterAsGuest();
            case "register" -> userProfilesService.register(body);
            case "logintoregistered" -> userProfilesService.loginToRegistered(body);
            case "logout" -> userProfilesService.logout(body);
            case "logoutasguest" -> userProfilesService.logoutAsGuest(body);
            case "addproducttocart" -> userProfilesService.addProductToCart(body);
            case "removeproductfromcart" -> userProfilesService.removeProductFromCart(body);
            case "changeproductquantity" -> userProfilesService.changeProductQuantity(body);
            case "viewcart" -> userProfilesService.viewCart(body);
            case "startpurchase" -> userProfilesService.startPurchase(body);
            case "payforpurchase" -> userProfilesService.payForPurchase(body);
            case "cancelpurchase" -> userProfilesService.cancelPurchase(body);
            case "getusertransactionhistory" -> userProfilesService.getUserTransactionHistory(body);
            default -> "Invalid endpoint";
        };
    }

    private String forwardStores(String endpoint, String body) {
        return switch(endpoint) {
            case "searchproductsglobally" -> storesService.searchProductsGlobally(body);
            case "searchproductsinstore" -> storesService.searchProductsInStore(body);
            case "addstore" -> storesService.addStore(body);
            case "openstore" -> storesService.openStore(body);
            case "closestore" -> storesService.closeStore(body);
            case "addproduct" -> storesService.addProduct(body);
            case "updateproduct" -> storesService.updateProduct(body);
            case "removeproduct" -> storesService.removeProduct(body);
            case "disableproduct" -> storesService.disableProduct(body);
            case "enableproduct" -> storesService.enableProduct(body);
            case "addowner" -> storesService.addOwner(body);
            case "addmanager" -> storesService.addManager(body);
            case "removeowner" -> storesService.removeOwner(body);
            case "removemanager" -> storesService.removeManager(body);
            case "addpermissiontomanager" -> storesService.addPermissionToManager(body);
            case "removepermissionfrommanager" -> storesService.removePermissionFromManager(body);
            case "getstorerolesinformation" -> storesService.getStoreRolesInformation(body);
            case "getstoretransactionhistory" -> storesService.getStoreTransactionHistory(body);
            case "setproductquantity" -> storesService.setProductQuantity(body);
            case "getstoreproducts" -> storesService.getStoreProducts(body);
            default -> "Invalid endpoint";
        };
    }

    private String forwardNotifications(String endpoint, String body) {
        return switch(endpoint) {
            case "sendnotification" -> notificationsService.sendNotification(body);
            case "setreadvalue" -> notificationsService.setReadValue(body);
            case "getunreadnotifications" -> notificationsService.getUnreadNotifications(body);
            case "getnotifications" -> notificationsService.getNotifications(body);
            case "deletenotification" -> notificationsService.deleteNotification(body);
            default -> "Invalid endpoint";
        };
    }

    private String forwardMarket(String endpoint, String body) {
        return switch(endpoint) {
            case "start" -> marketService.startMarket(body);
            case "shutdown" -> marketService.shutdown(body);
            default -> "Invalid endpoint";
        };
    }

    private String forwardExternal(String endpoint, String body) {
        return switch (endpoint) {
            case "sendshipment" -> externalServicesService.sendShipment(body);
            case "addshippingservice" -> externalServicesService.addShippingService(body);
            case "removeshippingservice" -> externalServicesService.removeShippingService(body);
            case "updateshippingservice" -> externalServicesService.updateShippingService(body);
            case "addpaymentservice" -> externalServicesService.addPaymentService(body);
            case "removepaymentservice" -> externalServicesService.removePaymentService(body);
            case "updatepaymentservice" -> externalServicesService.updatePaymentService(body);
            default -> "Invalid endpoint";
        };
    }

    private String forwardAuth(String endpoint, String body) {
        return switch (endpoint) {
            case "user" -> authenticationService.authenticateUser(body);
            case "guest" -> authenticationService.authenticateGuest(body);
            default -> "Invalid endpoint";
        };
    }
}
