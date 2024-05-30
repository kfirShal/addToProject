package com.amazonas.business.stores;

public class SalesPolicy {
    private String productID;
    private int productQuantity;
    private int discount;

    public SalesPolicy(String productID, int productQuantity,int discount){
        this.productID=productID;
        this.productQuantity = productQuantity;
        this.discount = discount;
    }

    public int getDiscount() {
        return discount;
    }
    public String getProductID() {
        return productID;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
