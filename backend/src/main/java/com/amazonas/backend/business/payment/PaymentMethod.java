package com.amazonas.backend.business.payment;

public interface PaymentMethod {
    String getCurrency();

    String getCardNumber();

    String getMonth();

    String getYear();

    String getHolder();

    String getCvv();

    String getId();
}
