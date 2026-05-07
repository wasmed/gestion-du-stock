package com.example.demo.service;

import com.example.demo.model.Produit;
import com.example.demo.model.StockProduit;
import com.example.demo.repository.ProduitRepository;
import com.example.demo.repository.StockProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockAiService {

    @Autowired
    private StockProduitRepository stockProduitRepository;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String analyseStock() {
        List<StockProduit> stocksCritiques = stockProduitRepository.findStocksCritiques();

        if (stocksCritiques.isEmpty()) {
            return "Le stock est bon.";
        }

        String listeProduits = stocksCritiques.stream()
                .map(s -> {
                    Produit p = s.getProduit();
                    String unite = p.getUnite() != null ? p.getUnite() : "";
                    String quantite = p.getQuantite() != null ? String.valueOf(p.getQuantite()) : "";
                    return p.getNom() + " (Conditionnement: " + quantite + " " + unite + ", Stock actuel: " + s.getStockActuel() + ")";
                })
                .collect(Collectors.joining(", "));

        String prompt = "En tant qu'expert logistique, analyse ces stocks critiques : [" + listeProduits + "]. Rédige une alerte et une liste de courses précise.";

        return callGeminiApi(prompt);
    }

    private String callGeminiApi(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt.replace("\"", "\\\"") + "\" }] }] }";
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'appel à l'API Gemini : " + e.getMessage();
        }
    }
}
