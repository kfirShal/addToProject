package com.amazonas.service;

import com.amazonas.business.payment.PaymentRequest;
import com.amazonas.business.payment.PaymentServiceController;
import com.amazonas.business.permissions.proxies.MarketProxy;
import com.amazonas.business.shipping.ShippingServiceController;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import com.amazonas.service.requests.Request;
import com.amazonas.service.requests.payment.PaymentServiceManagementRequest;
import com.amazonas.service.requests.shipping.ShipmentRequest;
import com.amazonas.service.requests.shipping.ShippingServiceManagementRequest;
import org.springframework.stereotype.Component;

@Component("externalServicesService")
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

    public String sendShipment(String json) {
        Request request = Request.from(json);
        ShipmentRequest shipmentRequest = JsonUtils.deserialize(request.payload(), ShipmentRequest.class);
        return new Response(shippingServiceController.sendShipment(shipmentRequest)).toJson();
    }

    public String processPayment(String json) {
        Request request = Request.from(json);
        PaymentRequest paymentRequest = JsonUtils.deserialize(request.payload(), PaymentRequest.class);
        return new Response(paymentServiceController.processPayment(paymentRequest)).toJson();
    }

    public String addShippingService(String json) {
        Request request = Request.from(json);
        ShippingServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), ShippingServiceManagementRequest.class);
        shippingServiceController.addShippingService(serviceRequest.serviceId(), serviceRequest.shippingService());
        return Response.getOk();
    }

    public String removeShippingService(String json) {
        Request request = Request.from(json);
        ShippingServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), ShippingServiceManagementRequest.class);
        shippingServiceController.removeShippingService(serviceRequest.serviceId());
        return Response.getOk();
    }

    public String updateShippingService(String json) {
        Request request = Request.from(json);
        ShippingServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), ShippingServiceManagementRequest.class);
        shippingServiceController.updateShippingService(serviceRequest.serviceId(), serviceRequest.shippingService());
        return Response.getOk();
    }

    public String addPaymentService(String json) {
        Request request = Request.from(json);
        PaymentServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), PaymentServiceManagementRequest.class);
        paymentServiceController.addPaymentService(serviceRequest.serviceId(), serviceRequest.paymentService());
        return Response.getOk();
    }

    public String removePaymentService(String json) {
        Request request = Request.from(json);
        PaymentServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), PaymentServiceManagementRequest.class);
        paymentServiceController.removePaymentService(serviceRequest.serviceId());
        return Response.getOk();
    }

    public String updatePaymentService(String json) {
        Request request = Request.from(json);
        PaymentServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), PaymentServiceManagementRequest.class);
        paymentServiceController.updatePaymentService(serviceRequest.serviceId(), serviceRequest.paymentService());
        return Response.getOk();
    }

}
