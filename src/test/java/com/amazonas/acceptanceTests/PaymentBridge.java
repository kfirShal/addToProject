package com.amazonas.acceptanceTests;

import com.amazonas.business.payment.PaymentMethod;

public interface PaymentBridge {
    boolean charge(PaymentMethod paymentMethod, double amount);

}
