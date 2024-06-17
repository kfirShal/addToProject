package com.amazonas.business.payment;

public class Paypal implements PaymentMethod{
    @Override
    public String getDetails() {
        return "Paypal";
    }
}
