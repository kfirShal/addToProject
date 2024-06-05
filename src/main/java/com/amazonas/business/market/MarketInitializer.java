package com.amazonas.business.market;

import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.payment.PaymentServiceController;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.business.shipping.ShippingServiceController;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Component("marketInitializer")
public class MarketInitializer {


    private final ShippingServiceController shippingServiceController;
    private final PaymentServiceController paymentServiceController;

    public MarketInitializer(ShippingServiceController shippingServiceController, PaymentServiceController paymentServiceController) {
        this.shippingServiceController = shippingServiceController;
        this.paymentServiceController = paymentServiceController;
    }

    public void start() {
        paymentServiceController.enableAllPaymentServices();
        paymentServiceController.enableAllPaymentMethods();
        shippingServiceController.enableAllShippingServices();
    }


    public void shutdown() {
        paymentServiceController.disableAllPaymentServices();
        paymentServiceController.disableAllPaymentMethods();
        shippingServiceController.disableAllShippingServices();
    }
}
