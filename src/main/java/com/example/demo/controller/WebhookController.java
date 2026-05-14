package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.service.CommandeService;
import com.example.demo.service.EmailService;
import com.example.demo.service.MollieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class WebhookController {

    @Autowired
    private MollieService mollieService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleMollieWebhook(@RequestParam("id") String paymentId) {
        try {
            Map<String, Object> paymentData = mollieService.getPayment(paymentId);
            if (paymentData != null) {
                String status = (String) paymentData.get("status");
                Map<String, Object> metadata = (Map<String, Object>) paymentData.get("metadata");

                if (metadata != null && metadata.containsKey("commandeId")) {
                    Long commandeId = Long.valueOf(metadata.get("commandeId").toString());
                    Commande commande = commandeService.findCommandeById(commandeId);

                    if (commande != null) {
                        if ("paid".equals(status)) {
                            commandeService.updateCommandeEtat(commandeId, EtatCommande.PAYEE);
                            if (commande.getClient() != null && commande.getClient().getEmail() != null) {
                                emailService.sendInvoiceEmail(commande.getClient().getEmail(), commande);
                            }
                        } else if ("canceled".equals(status) || "failed".equals(status) || "expired".equals(status)) {
                            String note = "[SYSTEM] Tentative de paiement Bancontact échouée (status: " + status + ")";
                            String currentComment = commande.getCommentaire();
                            if (currentComment == null) {
                                commande.setCommentaire(note);
                            } else {
                                commande.setCommentaire(currentComment + "\n" + note);
                            }
                            commandeService.saveCommande(commande);
                        }
                    }
                }
            }
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing webhook");
        }
    }
}
