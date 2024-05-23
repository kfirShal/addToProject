package com.amazonas.acceptanceTests;

import com.amazonas.business.payment.PaymentMethod;

public class RealPaymentBridge implements PaymentBridge{
    @Override
    public boolean charge(PaymentMethod paymentMethod, double amount) {
        return false;
    }
}
