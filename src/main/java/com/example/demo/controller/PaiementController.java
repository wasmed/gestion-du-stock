package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.ModePaiement;
import com.example.demo.service.CommandeService;
import com.example.demo.service.PaiementService;
import com.example.demo.service.QrCodeService;
import com.example.demo.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private StripeService stripeService;

    @Autowired
    private QrCodeService qrCodeService;

    @Value("${stripe.apiKey.public:pk_test_dummyPublic}")
    private String stripePublicKey;

    @GetMapping("/form/{commandeId}")
    public String showPaymentForm(@PathVariable Long commandeId, Model model) {
        Commande commande = commandeService.findCommandeById(commandeId);
        model.addAttribute("commande", commande);
        model.addAttribute("stripePublicKey", stripePublicKey);
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

    @PostMapping("/api/create-payment-intent")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestParam Long commandeId) {
        try {
            Commande commande = commandeService.findCommandeById(commandeId);
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(commande.getMontantTotal(), "Commande #" + commandeId);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/generate-qr/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> generateQrCode(@PathVariable Long id) {
        try {
            // For now, the QR code content is just the Order ID. In a real app, this might be a payment URL.
            String qrContent = "ORDER-" + id;
            String base64Image = qrCodeService.generateQrCodeImage(qrContent, 250, 250);

            Map<String, String> response = new HashMap<>();
            response.put("qrCodeImage", base64Image);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
             return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
