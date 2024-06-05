package com.amazonas.acceptanceTests;

import com.amazonas.business.permissions.actions.StoreActions;

public interface StoreBridge {
    boolean addProduct(String storeId,String productName, String productDescription);
    boolean removeProduct(String storeId, String productName);
    boolean searchProduct(String productName);
    boolean updateProduct(String storeId,String productName, String productDescription);
    boolean addPermission(String userId,String storeId, StoreActions action);
    boolean removePermission(String userId,String storeId, StoreActions action);
    boolean checkPermission(String userId,String storeId, StoreActions action);
}
