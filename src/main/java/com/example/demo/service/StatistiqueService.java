package com.example.demo.service;

import com.example.demo.dto.ItemStatDTO;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.LigneCommandeRepository;
import jakarta.annotation.PostConstruct;
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
        return ligneCommandeRepository.findTopSellingPlatsByCategory(com.example.demo.model.CategoriePlat.PLAT_PRINCIPAL);
    }

    public List<ItemStatDTO> getTopSellingBoissons() {
        return ligneCommandeRepository.findTopSellingPlatsByCategory(com.example.demo.model.CategoriePlat.BOISSON);
    }

    public List<ItemStatDTO> getTopSellingDesserts() {
        return ligneCommandeRepository.findTopSellingPlatsByCategory(com.example.demo.model.CategoriePlat.DESSERT);
    }

    public List<ItemStatDTO> getBottomSellingPlats() {
        return ligneCommandeRepository.findBottomSellingPlatsByCategory(com.example.demo.model.CategoriePlat.PLAT_PRINCIPAL);
    }

    public List<ItemStatDTO> getBottomSellingBoissons() {
        return ligneCommandeRepository.findBottomSellingPlatsByCategory(com.example.demo.model.CategoriePlat.BOISSON);
    }

    public List<ItemStatDTO> getBottomSellingDesserts() {
        return ligneCommandeRepository.findBottomSellingPlatsByCategory(com.example.demo.model.CategoriePlat.DESSERT);
    }

    public List<ItemStatDTO> getTopSellingMenus() {
        return ligneCommandeRepository.findTopSellingMenus();
    }

    public List<ItemStatDTO> getBottomSellingMenus() {
        return ligneCommandeRepository.findBottomSellingMenus();
    }

    public List<ItemStatDTO> getTopRatedPlats() {
        return feedbackRepository.findTopRatedPlatsByCategory(com.example.demo.model.CategoriePlat.PLAT_PRINCIPAL);
    }

    public List<ItemStatDTO> getTopRatedBoissons() {
        return feedbackRepository.findTopRatedPlatsByCategory(com.example.demo.model.CategoriePlat.BOISSON);
    }

    public List<ItemStatDTO> getTopRatedDesserts() {
        return feedbackRepository.findTopRatedPlatsByCategory(com.example.demo.model.CategoriePlat.DESSERT);
    }

    public List<ItemStatDTO> getBottomRatedPlats() {
        return feedbackRepository.findBottomRatedPlatsByCategory(com.example.demo.model.CategoriePlat.PLAT_PRINCIPAL);
    }

    public List<ItemStatDTO> getBottomRatedBoissons() {
        return feedbackRepository.findBottomRatedPlatsByCategory(com.example.demo.model.CategoriePlat.BOISSON);
    }

    public List<ItemStatDTO> getBottomRatedDesserts() {
        return feedbackRepository.findBottomRatedPlatsByCategory(com.example.demo.model.CategoriePlat.DESSERT);
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
        List<ItemStatDTO> topSellingBoissons = getTopSellingBoissons();
        List<ItemStatDTO> topSellingDesserts = getTopSellingDesserts();
        List<ItemStatDTO> bottomSellingPlats = getBottomSellingPlats();
        List<ItemStatDTO> topSellingMenus = getTopSellingMenus();
        List<ItemStatDTO> bottomSellingMenus = getBottomSellingMenus();
        List<ItemStatDTO> topRatedPlats = getTopRatedPlats();
        List<ItemStatDTO> bottomRatedPlats = getBottomRatedPlats();
        List<ItemStatDTO> topRatedMenus = getTopRatedMenus();
        List<ItemStatDTO> bottomRatedMenus = getBottomRatedMenus();

        String data = "Plats (principaux) top ventes : " + formatList(topSellingPlats) + " | " +
                "Boissons top ventes : " + formatList(topSellingBoissons) + " | " +
                "Desserts top ventes : " + formatList(topSellingDesserts) + " | " +
                "Plats flops ventes : " + formatList(bottomSellingPlats) + " | " +
                "Menus top ventes : " + formatList(topSellingMenus) + " | " +
                "Menus flops ventes : " + formatList(bottomSellingMenus) + " | " +
                "Plats mieux notés : " + formatList(topRatedPlats) + " | " +
                "Plats moins bien notés : " + formatList(bottomRatedPlats) + " | " +
                "Menus mieux notés : " + formatList(topRatedMenus) + " | " +
                "Menus moins bien notés : " + formatList(bottomRatedMenus);

        String prompt = "Tu es un expert en gestion de restaurant étoilé. Voici les statistiques de la semaine : [" + data + "]. " +
                "Rédige 3 recommandations marketing ou culinaires ULTRA-CONCISES pour augmenter le chiffre d'affaires. " +
                "RÈGLES ABSOLUES : AUCUN bla-bla d'introduction (ne dis pas 'Voici 3 recommandations'). " +
                "Fais uniquement une liste à puces directes avec du texte en gras pour le titre de l'idée.";

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

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey.trim();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String cleanPrompt = prompt.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
            String requestBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + cleanPrompt + "\" }] }] }";

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
