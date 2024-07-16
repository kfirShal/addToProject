package com.amazonas.backend.business.shipping;

import com.amazonas.common.dtos.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class ShippingService {

    private static final Logger log = LoggerFactory.getLogger(ShippingService.class);
    private final String serviceId;
    private final String externalSystemUrl = "https://damp-lynna-wsep-1984852e.koyeb.app/";
    private final RestTemplate restTemplate;

    public ShippingService() {
        serviceId = "default";
        restTemplate = new RestTemplate();
    }

    public boolean ship(Transaction transaction) {
        if(!handshake()) {
            return false;
        }

        String name = transaction.getUserId();
        // no support for these
        String address = "address";
        String city = "city";
        String country = "country";
        String zip = "zip";
        SupplyInfoDto dto = new SupplyInfoDto(name, address, city, country, zip);
        return supply(dto) >= 0;
    }

    private boolean handshake() {
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
            log.error("Failed to handshake with external system");
            return false;
        }

        return false;
    }

    private int supply(SupplyInfoDto supplyInfo) {
        // Form data
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action_type", "supply");
        formData.add("name", supplyInfo.getName());
        formData.add("address", supplyInfo.getAddress());
        formData.add("city", supplyInfo.getCity());
        formData.add("country", supplyInfo.getCountry());
        formData.add("zip", supplyInfo.getZip());

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
            log.error("Failed to supply product");
            return -1;
        }

        return -1; // Return -1 if supply failed
    }

    public int cancel_supply(int transaction_id) {
        // Form data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String formData = "action_type=cancel_supply&transaction_id=" + transaction_id;

        HttpEntity<String> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(externalSystemUrl, request, String.class);

            // Check the response and parse it to integer
            if (response.getStatusCode() == HttpStatus.OK) {
                return Integer.parseInt(response.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if cancellation failed
    }

    public String serviceId() {
        return serviceId;
    }
}
