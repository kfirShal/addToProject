package com.amazonas.service.requests;

import com.amazonas.business.shipping.ShippingService;

public record ShippingServiceManagementRequest(String serviceId, ShippingService shippingService) {
}
