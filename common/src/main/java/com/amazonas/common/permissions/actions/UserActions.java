package com.amazonas.common.permissions.actions;

public enum UserActions {

    ALL,

    // Shopping Cart
    VIEW_SHOPPING_CART,
    ADD_TO_SHOPPING_CART,
    REMOVE_FROM_SHOPPING_CART,
    UPDATE_SHOPPING_CART,

    // Transactions
    VIEW_USER_TRANSACTIONS,

    // Purchases
    START_PURCHASE,
    PAY_FOR_PURCHASE,
    CANCEL_PURCHASE,

    // Notifications
    SEND_NOTIFICATION,
    DELETE_NOTIFICATION,
    READ_NOTIFICATIONS,
    SET_NOTIFICATION_READ,

    // Suspends
    LIST_SUSPENDS,
    ADD_SUSPEND,
    REMOVE_SUSPEND,
    IS_CONTAINS_ID,
    DURATION,

}
