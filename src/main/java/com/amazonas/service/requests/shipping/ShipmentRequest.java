package com.amazonas.service.requests.shipping;

import com.amazonas.business.transactions.Transaction;

public record ShipmentRequest(Transaction transaction, String serviceId) {
}
