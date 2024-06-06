package com.amazonas.business.stores.policies;

public class minPolicy {
    private int quantity;
    public minPolicy(int quantity){
        this.quantity = quantity;
    }
    public int getQuantity(){
        return quantity;
    }
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
}
