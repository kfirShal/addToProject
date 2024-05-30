package com.amazonas.service.requests;

import com.amazonas.business.shipping.ShippingService;

public record ShippingServiceRequest(String serviceId, ShippingService shippingService) {
}
