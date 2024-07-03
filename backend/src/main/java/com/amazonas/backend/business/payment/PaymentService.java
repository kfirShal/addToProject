package com.amazonas.backend.business.payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentService {

    public boolean charge(PaymentMethod paymentMethod, Double amount){
        return true;
    }
}
