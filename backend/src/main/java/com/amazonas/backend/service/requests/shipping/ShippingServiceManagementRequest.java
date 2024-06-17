package com.amazonas.backend.service.requests.shipping;

import com.amazonas.backend.business.shipping.ShippingService;

public record ShippingServiceManagementRequest(String serviceId, ShippingService shippingService) {
}
