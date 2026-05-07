package com.example.demo.service;

import com.example.demo.dto.ItemStatDTO;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.LigneCommandeRepository;
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
public class StatistiqueService {

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    public List<ItemStatDTO> getTopSellingPlats() {
        return ligneCommandeRepository.findTopSellingPlats();
    }

    public List<ItemStatDTO> getBottomSellingPlats() {
        return ligneCommandeRepository.findBottomSellingPlats();
    }

    public List<ItemStatDTO> getTopSellingMenus() {
        return ligneCommandeRepository.findTopSellingMenus();
    }

    public List<ItemStatDTO> getBottomSellingMenus() {
        return ligneCommandeRepository.findBottomSellingMenus();
    }

    public List<ItemStatDTO> getTopRatedPlats() {
        return feedbackRepository.findTopRatedPlats();
    }

    public List<ItemStatDTO> getBottomRatedPlats() {
        return feedbackRepository.findBottomRatedPlats();
    }

    public List<ItemStatDTO> getTopRatedMenus() {
        return feedbackRepository.findTopRatedMenus();
    }

    public List<ItemStatDTO> getBottomRatedMenus() {
        return feedbackRepository.findBottomRatedMenus();
    }

    public List<ItemStatDTO> getCommandesPerDay() {
        return commandeRepository.findCommandesPerDay();
    }

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String analyseStatistiques() {
        List<ItemStatDTO> topSellingPlats = getTopSellingPlats();
        List<ItemStatDTO> bottomSellingPlats = getBottomSellingPlats();
        List<ItemStatDTO> topRatedPlats = getTopRatedPlats();
        List<ItemStatDTO> bottomRatedPlats = getBottomRatedPlats();

        String data = "Plats les plus vendus : " + formatList(topSellingPlats) + "; " +
                "Plats les moins vendus : " + formatList(bottomSellingPlats) + "; " +
                "Plats les mieux notés : " + formatList(topRatedPlats) + "; " +
                "Plats les moins bien notés : " + formatList(bottomRatedPlats);

        String prompt = "Tu es un expert en gestion de restaurant étoilé. Voici les données de mon restaurant cette semaine : [" + data + "]. Analyse ces données et donne-moi 3 recommandations marketing ou culinaires très courtes et concrètes pour augmenter mon chiffre d'affaires et satisfaire mes clients.";

        return callGeminiApi(prompt);
    }

    private String formatList(List<ItemStatDTO> list) {
        if (list == null || list.isEmpty()) {
            return "Aucune donnée";
        }
        return list.stream()
                .map(item -> item.getNom() + " (" + item.getValeur() + ")")
                .collect(Collectors.joining(", "));
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
