package com.amazonas.backend.business.payment;

public class Paypal implements PaymentMethod{
    @Override
    public String getDetails() {
        return "Paypal";
    }
}
