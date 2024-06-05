package com.amazonas.business.shipping;

import com.amazonas.business.transactions.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

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
