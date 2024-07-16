package com.amazonas.common.utils;

import com.amazonas.common.dtos.SupplyInfoDto;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class RealSupply {
    private String externalSystemUrl = "https://damp-lynna-wsep-1984852e.koyeb.app/";
    RestTemplate restTemplate;

    public RealSupply() {
        restTemplate = new RestTemplate();
    }

    public boolean handshake() {
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
            e.printStackTrace();
        }

        return false;
    }

    public int supply(SupplyInfoDto supplyInfo) {
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
            e.printStackTrace();
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
}
