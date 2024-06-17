package com.amazonas.backend.service.requests.shipping;

import com.amazonas.backend.business.transactions.Transaction;

public record ShipmentRequest(Transaction transaction, String serviceId) {
}
