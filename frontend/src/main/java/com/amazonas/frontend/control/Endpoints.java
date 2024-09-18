package com.amazonas.frontend.control;

import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.PurchaseRuleDTO.MultiplePurchaseRuleDTO;
import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;
import com.amazonas.common.dtos.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.List;
import com.amazonas.common.dtos.Notification;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;


@SuppressWarnings("SpellCheckingInspection")
public enum Endpoints {

    //TODO: REPLACE 'Void.class' WITH THE CORRECT RETURN TYPE

    // User Profiles Endpoints
    ENTER_AS_GUEST("userprofiles/enterasguest", String.class),
    REGISTER_USER("userprofiles/register", Boolean.class),
    LOGIN_TO_REGISTERED("userprofiles/logintoregistered", Boolean.class),
    LOGOUT("userprofiles/logout", Boolean.class),
    LOGOUT_AS_GUEST("userprofiles/logoutasguest", Boolean.class),
    ADD_PRODUCT_TO_CART("userprofiles/addproducttocart", Boolean.class),
    REMOVE_PRODUCT_FROM_CART("userprofiles/removeproductfromcart", Void.class),
    CHANGE_PRODUCT_QUANTITY("userprofiles/changeproductquantity", Void.class),
    VIEW_CART("userprofiles/viewcart", ShoppingCart.class),
    START_PURCHASE("userprofiles/startpurchase", Void.class),
    PAY_FOR_PURCHASE("userprofiles/payforpurchase", Void.class),
    CANCEL_PURCHASE("userprofiles/cancelpurchase", Void.class),
    GET_USER_TRANSACTION_HISTORY("userprofiles/getusertransactionhistory", Transaction.class),
    GET_USER_INFORMATION("userprofiles/getuserinformation", UserInformation.class),

    // Authentication Endpoints
    AUTHENTICATE_USER("auth/user", String.class),
    AUTHENTICATE_GUEST("auth/guest", String.class),

    // External Services Endpoints
    SEND_SHIPMENT("external/sendshipment", Boolean.class),
    ADD_SHIPPING_SERVICE("external/addshippingservice", Void.class),
    REMOVE_SHIPPING_SERVICE("external/removeshippingservice", Void.class),
    UPDATE_SHIPPING_SERVICE("external/updateshippingservice", Void.class),
    ADD_PAYMENT_SERVICE("external/addpaymentservice", Void.class),
    REMOVE_PAYMENT_SERVICE("external/removepaymentservice", Void.class),
    UPDATE_PAYMENT_SERVICE("external/updatepaymentservice", Void.class),

    // Market Endpoints
    START_MARKET("market/start", Void.class),
    SHUTDOWN_MARKET("market/shutdown", Void.class),

    // Notifications Endpoints
    SEND_NOTIFICATION("notifications/sendnotification", Void.class),
    SET_READ_VALUE("notifications/setreadvalue", Void.class),
    GET_UNREAD_NOTIFICATIONS("notifications/getunreadnotifications", Notification.class),
    GET_NOTIFICATIONS("notifications/getnotifications", Notification.class),
    DELETE_NOTIFICATION("notifications/deletenotification", Void.class),

    // Suspends Endpoints
    ADD_SUSPEND("suspends/addsuspend", Void.class),
    SUSPENDS_LIST("suspends/getsuspendlist", Suspend.class),
    REMOVE_SUSPEND("suspends/removesuspend", Suspend.class),


    // Stores Endpoints

    SEARCH_PRODUCTS_GLOBALLY("stores/searchproductsglobally", Product.class),
    SEARCH_PRODUCTS_IN_STORE("stores/searchproductsinstore", Product.class),
    SEARCH_STORES_GLOBALLY("stores/searchstoresglobally", StoreDetails.class),
    ADD_STORE("stores/addstore", String.class),
    OPEN_STORE("stores/openstore", Boolean.class),
    CLOSE_STORE("stores/closestore", Boolean.class),

    ADD_PRODUCT("stores/addproduct", Void.class),
    UPDATE_PRODUCT("stores/updateproduct", Void.class),
    REMOVE_PRODUCT("stores/removeproduct", Void.class),
    DISABLE_PRODUCT("stores/disableproduct", Void.class),
    ENABLE_PRODUCT("stores/enableproduct", Void.class),
    ADD_OWNER("stores/addowner", Void.class),
    ADD_MANAGER("stores/addmanager", Void.class),
    REMOVE_OWNER("stores/removeowner", Void.class),
    REMOVE_MANAGER("stores/removemanager", Void.class),
    ADD_PERMISSION_TO_MANAGER("stores/addpermissiontomanager", Boolean.class),
    REMOVE_PERMISSION_FROM_MANAGER("stores/removepermissionfrommanager", Boolean.class),
    GET_STORE_ROLES_INFORMATION("stores/getstorerolesinformation", StorePosition.class),
    GET_STORE_TRANSACTION_HISTORY("stores/getstoretransactionhistory", Transaction.class),
    SET_PRODUCT_QUANTITY("stores/setproductquantity", Void.class),
    GET_PRODUCT_QUANTITY("stores/getproductquantity", Integer.class),
    GET_STORE_PRODUCTS("stores/getstoreproducts", Types.GET_STORE_PRODUCTS_TYPE),
    GET_STORE_DETAILS("stores/getstoredetails", StoreDetails.class),
    GET_PRODUCT("stores/getproduct", Product.class),
    ADD_DISCOUNT_RULE_CFG("stores/adddiscountrulebycfg", String.class),
    GET_DISCOUNT_RULE_CFG("stores/getcfgdiscountrule", String.class),
    ADD_DISCOUNT_RULE_DTO("stores/adddiscountrulebydto", String.class),
    GET_DISCOUNT_RULE_DTO("stores/getdtodiscountrule", DiscountComponentDTO.class),
    REMOVE_DISCOUNT_RULE("stores/removediscountrule", Boolean.class),
    ADD_PURCHASE_POLICY("stores/addpuchasepolicy", Void.class),
    GET_PURCHASE_POLICY("stores/getpurchasepolicy", MultiplePurchaseRuleDTO.class),
    REMOVE_PURCHASE_POLICY("stores/removepuchasepolicy", Boolean.class),

    //Permissions Endpoints
    GET_USER_PERMISSIONS("permissions/getuserpermissions", UserPermissionsProfile.class),
    GET_GUEST_PERMISSIONS("permissions/getguestpermissions", DefaultPermissionsProfile.class),
    IS_ADMIN("permissions/isadmin", Boolean.class);

    private final String location;
    private final Type returnType;

    Endpoints(String location, Type returnType) {
        this.location = location;
        this.returnType = returnType;
    }

    public String location() {
        return location;
    }

    public Type returnType() {
        return returnType;
    }

    private static class Types {
        private static final Type GET_STORE_PRODUCTS_TYPE = new TypeToken<Map<Boolean, List<Product>>>() {}.getType();
    }
}
