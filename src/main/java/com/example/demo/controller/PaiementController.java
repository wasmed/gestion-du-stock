package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.ModePaiement;
import com.example.demo.service.CommandeService;
import com.example.demo.service.PaiementService;
import com.example.demo.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/paiement")
@PreAuthorize("hasRole('SERVEUR')") // Seul le serveur peut gérer les paiements
public class PaiementController {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/form/{commandeId}")
    public String showPaymentForm(@PathVariable Long commandeId, Model model) {
        Commande commande = commandeService.findCommandeById(commandeId);
        model.addAttribute("commande", commande);
        return "paiement/form";
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam Long commandeId,
                                 @RequestParam(required = false) Double pourboire,
                                 @RequestParam ModePaiement modePaiement,
                                 Principal principal) {
        paiementService.processPayment(commandeId, pourboire, modePaiement);
        return "redirect:/orders"; // Retourne à la liste des commandes
    }

    // --- API Endpoints for AJAX ---

    @GetMapping("/api/generate-qr/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateQrCode(@PathVariable Long id) {
        try {
            // URL de simulation de paiement
            String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            String qrContent = baseUrl + "/client/paiement/simulation/" + id;
            String base64Image = qrCodeService.generateQrCodeImage(qrContent, 250, 250);

            Map<String, String> response = new HashMap<>();
            response.put("qrCodeImage", base64Image);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
             return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
