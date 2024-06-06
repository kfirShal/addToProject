package com.amazonas.service;

import com.amazonas.business.payment.PaymentRequest;
import com.amazonas.business.payment.PaymentServiceController;
import com.amazonas.business.permissions.proxies.MarketProxy;
import com.amazonas.business.shipping.ShippingServiceController;
import com.amazonas.service.requests.payment.PaymentServiceManagementRequest;
import com.amazonas.service.requests.shipping.ShipmentRequest;
import com.amazonas.service.requests.shipping.ShippingServiceManagementRequest;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

@Component
public class ExternalServicesService {
    private final ShippingServiceController shippingServiceController;
    private final PaymentServiceController paymentServiceController;
    private final MarketProxy marketProxy;

    public ExternalServicesService(ShippingServiceController shippingServiceController, PaymentServiceController paymentServiceController, MarketProxy marketProxy) {
        this.shippingServiceController = shippingServiceController;
        this.paymentServiceController = paymentServiceController;
        this.marketProxy = marketProxy;
    }

    //TODO: USE PROXY

    public String sendShipment(String json){
        ShipmentRequest request = JsonUtils.deserialize(json, ShipmentRequest.class);
        return new Response(shippingServiceController.sendShipment(request)).toJson();
    }

    public String processPayment(String json){
        PaymentRequest request = JsonUtils.deserialize(json, ShipmentRequest.class);
        return new Response(paymentServiceController.processPayment(request)).toJson();
    }

    public String addShippingService(String json) {
        ShippingServiceManagementRequest request = JsonUtils.deserialize(json, ShippingServiceManagementRequest.class);
        shippingServiceController.addShippingService(request.serviceId(), request.shippingService());
        return new Response(true).toJson();
    }

    public String removeShippingService(String json) {
        ShippingServiceManagementRequest request = JsonUtils.deserialize(json, ShippingServiceManagementRequest.class);
        shippingServiceController.removeShippingService(request.serviceId());
        return new Response(true).toJson();
    }

    public String updateShippingService(String json) {
        ShippingServiceManagementRequest request = JsonUtils.deserialize(json, ShippingServiceManagementRequest.class);
        shippingServiceController.updateShippingService(request.serviceId(), request.shippingService());
        return new Response(true).toJson();
    }

    public String addPaymentService(String json) {
        PaymentServiceManagementRequest request = JsonUtils.deserialize(json, PaymentServiceManagementRequest.class);
        paymentServiceController.addPaymentService(request.serviceId(), request.paymentService());
        return new Response(true).toJson();
    }

    public String removePaymentService(String json) {
        PaymentServiceManagementRequest request = JsonUtils.deserialize(json, PaymentServiceManagementRequest.class);
        paymentServiceController.removePaymentService(request.serviceId());
        return new Response(true).toJson();
    }

    public String updatePaymentService(String json) {
        PaymentServiceManagementRequest request = JsonUtils.deserialize(json, PaymentServiceManagementRequest.class);
        paymentServiceController.updatePaymentService(request.serviceId(), request.paymentService());
        return new Response(true).toJson();
    }
}
