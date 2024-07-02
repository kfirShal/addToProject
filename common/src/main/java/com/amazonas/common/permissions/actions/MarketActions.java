package com.amazonas.common.permissions.actions;

public enum MarketActions {

    // general actions
    SEARCH_PRODUCTS,
    VIEW_PRODUCTS,
    VIEW_STORES,

    // market management
    SHUTDOWN_MARKET,
    RESTART,
    START_MARKET,

    // store management
    CREATE_STORE,
    REMOVE_STORE,

    // shipping management
    ADD_SHIPPING_SERVICE,
    REMOVE_SHIPPING_SERVICE,
    UPDATE_SHIPPING_SERVICE,
    ENABLE_SHIPPING_SERVICE,
    DISABLE_SHIPPING_SERVICE,

    // payment service management
    ADD_PAYMENT_SERVICE,
    REMOVE_PAYMENT_SERVICE,
    UPDATE_PAYMENT_SERVICE,
    DISABLE_PAYMENT_SERVICE,
    ENABLE_PAYMENT_SERVICE,

    // payment method management
    ADD_PAYMENT_METHOD,
    REMOVE_PAYMENT_METHOD,
    UPDATE_PAYMENT_METHOD,
    ENABLE_PAYMENT_METHOD,
    DISABLE_PAYMENT_METHOD,

}
