package com.amazonas.business.shipping;

import com.amazonas.business.transactions.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShippingService {

    public boolean ship(Transaction transaction) {
        return true;
    }
}
