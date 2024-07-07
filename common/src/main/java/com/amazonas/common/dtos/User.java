package com.amazonas.common.dtos;

public abstract class User {

    private String initialId;
    private PaymentMethod paymentMethod;

    public User(String initialId){
        this.initialId = initialId;
    }

    public String getUserId(){
        return initialId;
    }
    public PaymentMethod getPaymentMethod() {return paymentMethod;}
}
