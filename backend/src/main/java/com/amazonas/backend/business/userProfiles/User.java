package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.payment.PaymentMethod;

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
