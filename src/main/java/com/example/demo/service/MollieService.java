package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class MollieService {

    @Value("${mollie.api.key:test_dummy}")
    private String mollieApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String MOLLIE_API_URL = "https://api.mollie.com/v2/payments";

    public String createPaymentAndGetCheckoutUrl(Long commandeId, Double amount, String redirectUrl, Double pourboire) {
        if ("test_dummy".equals(mollieApiKey) || mollieApiKey.isEmpty()) {
            return redirectUrl.replace("/mollie-return/", "/simulation/") + "?pourboire=" + pourboire;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(mollieApiKey);

        Map<String, Object> amountMap = new HashMap<>();
        amountMap.put("currency", "EUR");
        amountMap.put("value", String.format(java.util.Locale.US, "%.2f", amount));

        Map<String, Object> body = new HashMap<>();
        body.put("amount", amountMap);
        body.put("description", "Commande #" + commandeId + (pourboire > 0 ? " (incl. pourboire)" : ""));
        body.put("redirectUrl", redirectUrl);
        body.put("method", "bancontact");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("commandeId", commandeId);
        metadata.put("pourboire", pourboire);
        body.put("metadata", metadata);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(MOLLIE_API_URL, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> links = (Map<String, Object>) response.getBody().get("_links");
                if (links != null) {
                    Map<String, String> checkout = (Map<String, String>) links.get("checkout");
                    if (checkout != null) {
                        return checkout.get("href");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur Mollie API: " + e.getMessage());
        }

        // Fallback
        return redirectUrl.replace("/mollie-return/", "/simulation/") + "?pourboire=" + pourboire;
    }

    public boolean isPaymentPaid(String paymentId) {
        if ("test_dummy".equals(mollieApiKey) || mollieApiKey.isEmpty()) {
            return true;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(mollieApiKey);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(MOLLIE_API_URL + "/" + paymentId, HttpMethod.GET, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String status = (String) response.getBody().get("status");
                return "paid".equals(status);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur Mollie API check: " + e.getMessage());
        }
        return false;
    }
}
