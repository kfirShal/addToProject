package com.amazonas.backend.business.payment;

public class CreditCard implements PaymentMethod {
    public String getDetails(){
        return "Credit Card";
    }
}
