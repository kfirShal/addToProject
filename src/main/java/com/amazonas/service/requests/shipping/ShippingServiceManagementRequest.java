package com.amazonas.service.requests.shipping;

import com.amazonas.business.shipping.ShippingService;

public record ShippingServiceManagementRequest(String serviceId, ShippingService shippingService) {
}
