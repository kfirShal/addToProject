package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;

public abstract class User {

    private String initialId;
    private ShoppingCart cart;
    private PaymentMethod paymentMethod;
    public User(String initialId){
        this.initialId = initialId;
        cart = new ShoppingCart();
    }

    public void addToCart(String storeName, int productId, Product product, int quantity){
        cart.addProduct(storeName,productId,product,quantity);

    }

    public void getBasket(String storeName){
        cart.getBasket(storeName);
    }

    public void removeProductFromBasket(String storeName,int productId){
        cart.removeProduct(storeName,productId);
    }

    public Boolean isProductExists(String storeName, int productId){
        return cart.isProductExists(storeName,productId);
    }
    public void changeProductQuantity(String storeName, int productId,int quantity){
        cart.changeProductQuantity(storeName,productId,quantity);
    }


    public ShoppingCart getCart() {
        return cart;
    }

    public String getUserId(){
        return initialId;
    }
    public PaymentMethod getPaymentMethod() {return paymentMethod;}
}
