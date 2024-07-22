package com.amazonas.backend.business.payment;

import com.amazonas.backend.ConfigurationValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    //private final String externalSystemUrl = "https://damp-lynna-wsep-1984852e.koyeb.app/";
    private final String externalSystemUrl = ConfigurationValues.getProperty("PAYMENT_SERVICE_URL");
    private final RestTemplate restTemplate;

    public PaymentService() {
        restTemplate = new RestTemplate();
    }

    public boolean charge(PaymentMethod paymentMethod, Double amount) {
        if(!handshake()){
            return false;
        }
        return payment(paymentMethod, amount) >= 0;
    }

    private boolean handshake(){
        // Form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("action_type=handshake", headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Check if response is "OK"
            if (response.getStatusCode() == HttpStatus.OK && "OK".equals(response.getBody())) {
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to handshake with external system", e);
            return false;
        }
        return false;
    }

    private int payment(PaymentMethod paymentMethod, double totalPrice)  {
        // Form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action_type", "pay");
        formData.add("amount", Double.toString(totalPrice));
        formData.add("currency", paymentMethod.getCurrency());
        formData.add("card_number", paymentMethod.getCardNumber());
        formData.add("month", paymentMethod.getMonth());
        formData.add("year", paymentMethod.getYear());
        formData.add("holder", paymentMethod.getHolder());
        formData.add("cvv", paymentMethod.getCvv());
        formData.add("id", paymentMethod.getId());

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Request entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            // Send POST request
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Parse the response to an integer
            if (response.getStatusCode() == HttpStatus.OK) {
                return Integer.parseInt(response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to pay with credit card", e);
            return -1;
        }

        return -1; // Return -1 if payment failed
    }

    public int cancel_pay(int transaction_id)  {
        // Form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formData = "action_type=cancel_pay&transaction_id=" + transaction_id;

        HttpEntity<String> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Check the response and parse it to integer
            if (response.getStatusCode() == HttpStatus.OK) {
                return Integer.parseInt(response.getBody());
            }
        } catch (Exception e) {
            log.error("Failed to cancel payment", e);
            return -1;
        }

        return -1; // Return -1 if cancellation failed
    }
}
