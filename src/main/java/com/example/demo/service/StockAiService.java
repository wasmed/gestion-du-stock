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
            return "Le stock est bon. Aucune alerte.";
        }

        String listeProduits = stocksCritiques.stream()
                .map(s -> {
                    Produit p = s.getProduit();
                    String unite = p.getUnite() != null ? p.getUnite() : "";
                    String quantite = p.getQuantite() != null ? String.valueOf(p.getQuantite()) : "1";

                    return p.getNom() + " (Stock actuel: " + s.getStockActuel() + " paquets, " +
                            "Stock minimum requis: " + s.getStockMinimum() + " paquets. " +
                            "[Rappel: 1 paquet = " + quantite + " " + unite + "])";
                })
                .collect(Collectors.joining(" | "));

        java.time.DayOfWeek jour = java.time.LocalDate.now().getDayOfWeek();
        boolean isWeekend = (jour == java.time.DayOfWeek.FRIDAY || jour == java.time.DayOfWeek.SATURDAY || jour == java.time.DayOfWeek.SUNDAY);
        String contexteJour = isWeekend ? "C'est le week-end (" + jour + ")." : "Nous sommes en semaine (" + jour + ").";

        // 2. Le prompt ultime avec la règle mathématique des fournisseurs
        String prompt = "Tu es l'assistant de cuisine. " + contexteJour + " Voici les stocks critiques : [" + listeProduits + "]. " +
                "Rédige un rapport ULTRA-CONCIS. AUCUN bla-bla, AUCUN format email. " +
                "2 sections avec des listes à puces : " +
                "1) 🚨 Alertes (1 courte phrase par produit). " +
                "2) 🛒 Liste de courses. " +
                "RÈGLE DE CALCUL POUR LA LISTE : " +
                "- Étape 1 : Calcule la différence (Stock minimum requis - Stock actuel). " +
                (isWeekend ? "- Étape 2 : Puisque c'est le week-end, multiplie ce résultat par 1.30 (marge de sécurité). " : "") +
                "- Étape 3 (CRUCIAL) : ARRONDIS TOUJOURS au nombre entier supérieur pour obtenir le nombre de paquets à commander (ex: si le calcul donne 1.2, il faut 2 paquets). " +
                "Affiche les éléments de la liste de courses STRICTEMENT sous ce format : '- [Nom du produit] : [Nombre entier] paquets (soit [Total] [Unité de mesure])'.";

        return callGeminiApi(prompt);
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
