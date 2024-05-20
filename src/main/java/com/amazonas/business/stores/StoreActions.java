package com.amazonas.business.stores;

public enum StoreActions {
    VIEW_STORE,

    // product management
    ADD_PRODUCT,
    REMOVE_PRODUCT,
    UPDATE_PRODUCT,
    ENABLE_PRODUCT,
    DISABLE_PRODUCT,

    // store administration,
    CLOSE_STORE,
    OPEN_STORE,
    UPDATE_STORE_INFORMATION,
    VIEW_STORE_TRANSACTIONS,

    // permissions management
    ADD_OWNER,
    REMOVE_OWNER,
    ADD_MANAGER,
    REMOVE_MANAGER,

    // store controller actions
    ADD_STORE,
    REMOVE_STORE,
    UPDATE_STORE,
    ENABLE_STORE,
    DISABLE_STORE,

}
