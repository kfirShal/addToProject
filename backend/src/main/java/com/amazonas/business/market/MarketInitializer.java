package com.amazonas.business.market;

import com.amazonas.business.payment.PaymentServiceController;
import com.amazonas.business.shipping.ShippingServiceController;
import org.springframework.stereotype.Component;

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
