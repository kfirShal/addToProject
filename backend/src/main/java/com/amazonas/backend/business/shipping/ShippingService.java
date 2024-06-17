package com.amazonas.backend.business.shipping;

import com.amazonas.backend.business.transactions.Transaction;
import org.springframework.stereotype.Component;

@Component
public class ShippingService {

    private String serviceId;

    public ShippingService() {
        this.serviceId = "default";
    }

    public boolean ship(Transaction transaction) {
        return true;
    }

    public String serviceId() {
        return serviceId;
    }
}
