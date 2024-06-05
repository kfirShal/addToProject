package com.amazonas.service.requests;

import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.transactions.Transaction;

public record ShipmentRequest(Transaction transaction, String serviceId) {
}
