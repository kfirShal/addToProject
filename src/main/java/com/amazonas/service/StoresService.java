package com.amazonas.service;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresController;
import com.amazonas.exceptions.StoreException;
import com.amazonas.service.requests.StoreCreationRequest;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Condition;

@Component
public class StoresService {

    private final StoresController controller;

    public String addStore(String storeJson){
        StoreCreationRequest toAdd = JsonUtils.deserialize(storeJson, StoreCreationRequest.class);
        try {
            controller.addStore(toAdd.ownerId(), toAdd.storeName(), toAdd.description());
            return new Response(true).toJson();
        }
        catch (StoreException e){
            return Response.getErrorResponse(e).toJson();
        }

    }
    public String openStore(String storeId){
        return new Response(controller.openStore(storeId)).toJson();
    }
    public String closeStore(String storeId){
        return new Response(controller.closeStore(storeId)).toJson();
    }
    public void addProduct(String storeId, String toAddJson) throws StoreException {
        Product toAdd = JsonUtils.deserialize(toAddJson,Product.class);
        controller.addProduct(storeId,toAdd);
    }
    public void updateProduct(String storeId,String toUpdateJson) throws StoreException {
        Product toUpdate = JsonUtils.deserialize(toUpdateJson,Product.class);
        controller.updateProduct(storeId,toUpdate);
    }
    public void removeProduct(String storeId,String toRemoveJson) throws StoreException {
        controller.removeProduct(storeId,toRemoveJson);
    }
    public void disableProduct(String storeId,String productId){
        controller.disableProduct(storeId,productId);
    }
    public void enableProduct(String storeId,String productId){
        controller.enableProduct(storeId,productId);
    }
    public void addOwner(String username, String storeId,String logged){
        controller.addOwner(username,storeId,logged);
    }
    public void addManager(String logged, String storeId,String username){
        controller.addManager(logged,storeId,username);
    }
    public void removeOwner(String username,String storeId,String logged){
        controller.removeOwner(username,storeId,logged);
    }
    public void removeManager(String logged,String storeId,String username){
        controller.removeManager(logged,storeId,username);
    }
    public void cancelReservation(String storeId, String username){
        controller.cancelReservation(storeId,username);
    }
    public void setReservationTimeoutSeconds(String storeId, long time){
        controller.setReservationTimeoutSeconds(storeId, time);
    }
    public boolean addPermissionToManager(String storeId,String managerId, StoreActions actions) throws StoreException {
        return controller.addPermissionToManager(storeId,managerId,actions);
    }
    public boolean removePermissionFromManager(String storeId,String managerId, StoreActions actions) throws StoreException{
        return controller.removePermissionFromManager(storeId,managerId,actions);
    }
    public StoresService(StoresController storeProxy) {
        this.controller = storeProxy;
    }
}
