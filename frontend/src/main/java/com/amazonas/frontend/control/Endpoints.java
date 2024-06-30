package com.amazonas.frontend.control;

import com.amazonas.common.dtos.Product;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

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
    VIEW_CART("userprofiles/viewcart", Void.class),
    START_PURCHASE("userprofiles/startpurchase", Void.class),
    PAY_FOR_PURCHASE("userprofiles/payforpurchase", Void.class),
    CANCEL_PURCHASE("userprofiles/cancelpurchase", Void.class),
    GET_USER_TRANSACTION_HISTORY("userprofiles/getusertransactionhistory", Void.class),

    // Authentication Endpoints
    AUTHENTICATE_USER("auth/user", String.class),
    AUTHENTICATE_GUEST("auth/guest", String.class),

    // External Services Endpoints
    SEND_SHIPMENT("external/sendshipment", Void.class),
    PROCESS_PAYMENT("external/processpayment", Void.class),
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
    GET_UNREAD_NOTIFICATIONS("notifications/getunreadnotifications", Void.class),
    GET_NOTIFICATIONS("notifications/getnotifications", Void.class),
    DELETE_NOTIFICATION("notifications/deletenotification", Void.class),

    // Stores Endpoints
    SEARCH_PRODUCTS_GLOBALLY("stores/searchproductsglobally", Void.class),
    SEARCH_PRODUCTS_IN_STORE("stores/searchproductsinstore", Void.class),
    ADD_STORE("stores/addstore", Void.class),
    OPEN_STORE("stores/openstore", Void.class),
    CLOSE_STORE("stores/closestore", Void.class),
    ADD_PRODUCT("stores/addproduct", Void.class),
    UPDATE_PRODUCT("stores/updateproduct", Void.class),
    REMOVE_PRODUCT("stores/removeproduct", Void.class),
    DISABLE_PRODUCT("stores/disableproduct", Void.class),
    ENABLE_PRODUCT("stores/enableproduct", Void.class),
    ADD_OWNER("stores/addowner", Void.class),
    ADD_MANAGER("stores/addmanager", Void.class),
    REMOVE_OWNER("stores/removeowner", Void.class),
    REMOVE_MANAGER("stores/removemanager", Void.class),
    ADD_PERMISSION_TO_MANAGER("stores/addpermissiontomanager", Void.class),
    REMOVE_PERMISSION_FROM_MANAGER("stores/removepermissionfrommanager", Void.class),
    GET_STORE_ROLES_INFORMATION("stores/getstorerolesinformation", Void.class),
    GET_STORE_TRANSACTION_HISTORY("stores/getstoretransactionhistory", Void.class),
    SET_PRODUCT_QUANTITY("stores/setproductquantity", Void.class),
    GET_STORE_PRODUCTS("stores/getstoreproducts", Types.GET_STORE_PRODUCTS_TYPE),
    GET_PRODUCT_QUANTITY("stores/getproductquantity", Integer.class);

    private final String location;
    private final Class<?> returnType;

    Endpoints(String location, Class<?> returnType) {
        this.location = location;
        this.returnType = returnType;
    }

    public String location() {
        return location;
    }
    
    @SuppressWarnings("unchecked")
    public <T> Class<T> returnType() {
        return (Class<T>) returnType;
    }

    private static class Types {
        private static final Class<? extends Type> GET_STORE_PRODUCTS_TYPE = new TypeToken<Map<Boolean,Set<Product>>>() {}.getType().getClass();
    }
}
