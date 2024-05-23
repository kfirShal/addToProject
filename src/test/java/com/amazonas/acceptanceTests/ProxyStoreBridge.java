package com.amazonas.acceptanceTests;

import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;

public class ProxyStoreBridge implements StoreBridge{

    @Override
    public boolean addProduct(String storeId, String productName, String productDescription) {
        return false;
    }

    @Override
    public boolean removeProduct(String storeId, String productName) {
        return false;
    }

    @Override
    public boolean searchProduct(String productName) {
        return false;
    }

    @Override
    public boolean updateProduct(String storeId, String productName, String productDescription) {
        return false;
    }

    @Override
    public boolean addPermission(String userId, String storeId, StoreActions action) {
        return false;
    }

    @Override
    public boolean removePermission(String userId, String storeId, StoreActions action) {
        return false;
    }

    @Override
    public boolean checkPermission(String userId, String storeId, StoreActions action) {
        return false;
    }
}
