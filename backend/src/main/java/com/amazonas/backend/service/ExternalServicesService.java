package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.proxies.ExternalServicesProxy;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.service.requests.payment.PaymentServiceManagementRequest;
import com.amazonas.backend.service.requests.shipping.ShippingServiceManagementRequest;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.shipping.ShipmentRequest;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

@Component("externalServicesService")
public class ExternalServicesService {

    private final ExternalServicesProxy proxy;

    public ExternalServicesService(ExternalServicesProxy externalServicesProxy) {
        proxy = externalServicesProxy;
    }

    public String sendShipment(String json) {
        Request request = Request.from(json);
        try{
            ShipmentRequest shipmentRequest = ShipmentRequest.from(request.payload());
            boolean result = proxy.sendShipment(shipmentRequest.transactionId(), shipmentRequest.serviceId(),shipmentRequest.storeId(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String addShippingService(String json) {
        Request request = Request.from(json);
        try{
            ShippingServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), ShippingServiceManagementRequest.class);
            proxy.addShippingService(serviceRequest.serviceId(), serviceRequest.shippingService(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String removeShippingService(String json) {
        Request request = Request.from(json);
        try{
            ShippingServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), ShippingServiceManagementRequest.class);
            proxy.removeShippingService(serviceRequest.serviceId(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String updateShippingService(String json) {
        Request request = Request.from(json);
        try{
            ShippingServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), ShippingServiceManagementRequest.class);
            proxy.updateShippingService(serviceRequest.serviceId(), serviceRequest.shippingService(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String addPaymentService(String json) {
        Request request = Request.from(json);
        try{
            PaymentServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), PaymentServiceManagementRequest.class);
            proxy.addPaymentService(serviceRequest.serviceId(), serviceRequest.paymentService(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String removePaymentService(String json) {
        Request request = Request.from(json);
        try{
            PaymentServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), PaymentServiceManagementRequest.class);
            proxy.removePaymentService(serviceRequest.serviceId(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String updatePaymentService(String json) {
        Request request = Request.from(json);
        try{
            PaymentServiceManagementRequest serviceRequest = JsonUtils.deserialize(request.payload(), PaymentServiceManagementRequest.class);
            proxy.updatePaymentService(serviceRequest.serviceId(), serviceRequest.paymentService(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException e) {
            return Response.getError(e);
        }
    }
}
