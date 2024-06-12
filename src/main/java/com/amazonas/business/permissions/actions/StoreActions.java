package com.amazonas.business.permissions.actions;

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
    ADD_PERMISSION_TO_MANAGER,

    // store controller actions
    ADD_STORE,
    REMOVE_STORE,
    UPDATE_STORE,
    ENABLE_STORE,
    DISABLE_STORE,

    SET_RESERVATION_TIMEOUT, REMOVE_PERMISSION_FROM_MANAGER, VIEW_ROLES_INFORMATION,

}
