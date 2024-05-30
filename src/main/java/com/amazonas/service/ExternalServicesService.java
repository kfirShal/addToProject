package com.amazonas.service;

import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.payment.PaymentServiceController;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.shipping.ShippingServiceController;
import com.amazonas.service.requests.PaymentServiceRequest;
import com.amazonas.service.requests.ShippingServiceRequest;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

@Component
public class ExternalServicesService {
    private final ShippingServiceController shippingServiceController;
    private final PaymentServiceController paymentServiceController;

    public ExternalServicesService(ShippingServiceController shippingServiceController, PaymentServiceController paymentServiceController) {
        this.shippingServiceController = shippingServiceController;
        this.paymentServiceController = paymentServiceController;
    }

    public String addShippingService(String json) {
        ShippingServiceRequest request = JsonUtils.deserialize(json, ShippingServiceRequest.class);
        shippingServiceController.addShippingService(request.serviceId(), request.shippingService());
        return new Response(true).toJson();
    }

    public String removeShippingService(String json) {
        ShippingServiceRequest request = JsonUtils.deserialize(json, ShippingServiceRequest.class);
        shippingServiceController.removeShippingService(request.serviceId());
        return new Response(true).toJson();
    }

    public String updateShippingService(String json) {
        ShippingServiceRequest request = JsonUtils.deserialize(json, ShippingServiceRequest.class);
        shippingServiceController.updateShippingService(request.serviceId(), request.shippingService());
        return new Response(true).toJson();
    }

    public String addPaymentService(String json) {
        PaymentServiceRequest request = JsonUtils.deserialize(json, PaymentServiceRequest.class);
        paymentServiceController.addPaymentService(request.serviceId(), request.paymentService());
        return new Response(true).toJson();
    }

    public String removePaymentService(String json) {
        PaymentServiceRequest request = JsonUtils.deserialize(json, PaymentServiceRequest.class);
        paymentServiceController.removePaymentService(request.serviceId());
        return new Response(true).toJson();
    }

    public String updatePaymentService(String json) {
        PaymentServiceRequest request = JsonUtils.deserialize(json, PaymentServiceRequest.class);
        paymentServiceController.updatePaymentService(request.serviceId(), request.paymentService());
        return new Response(true).toJson();
    }


}
