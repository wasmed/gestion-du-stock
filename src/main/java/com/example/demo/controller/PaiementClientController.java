package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.ModePaiement;
import com.example.demo.service.CommandeService;
import com.example.demo.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/client/paiement")
public class PaiementClientController {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PaiementService paiementService;

    @GetMapping("/simulation/{commandeId}")
    public String showSimulationPage(@PathVariable Long commandeId, Model model) {
        Commande commande = commandeService.findCommandeById(commandeId);
        model.addAttribute("commande", commande);
        return "client/paiement-simulation";
    }

    @PostMapping("/simulation/process")
    public String processSimulationPayment(@RequestParam Long commandeId,
                                           @RequestParam(required = false, defaultValue = "0.0") Double pourboire,
                                           @RequestParam String methodePaiement, // NOUVEAU PARAMÈTRE !
                                           RedirectAttributes redirectAttributes) {

        // On traduit le choix du HTML ("QR" ou "CB") vers notre Enum Java
        ModePaiement mode = "CB".equals(methodePaiement) ? ModePaiement.CARTE_BANCAIRE : ModePaiement.QR_CODE;

        // On enregistre le paiement avec le bon mode !
        try {
            paiementService.processPayment(commandeId, pourboire, mode);
            return "redirect:/client/paiement/succes/" + commandeId;
        } catch (Exception e) {
            return "redirect:/client/paiement/echec/" + commandeId;
        }
    }

    @Autowired
    private com.example.demo.service.MollieService mollieService;

    @GetMapping("/init/{commandeId}")
    public String initPayment(@PathVariable Long commandeId,
                              @RequestParam(required = false, defaultValue = "0.0") Double pourboire) {
        Commande commande = commandeService.findCommandeById(commandeId);
        double totalAmount = commande.getMontantTotal() + pourboire;

        String baseUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String redirectUrl = baseUrl + "/client/paiement/mollie-return/" + commandeId + "?pourboire=" + pourboire;
        String webhookUrl = baseUrl + "/api/payments/webhook";

        String checkoutUrl = mollieService.createPaymentAndGetCheckoutUrl(commandeId, totalAmount, redirectUrl, webhookUrl, pourboire);

        return "redirect:" + checkoutUrl;
    }

    @GetMapping("/mollie-return/{commandeId}")
    public String mollieReturn(@PathVariable Long commandeId,
                               @RequestParam(required = false, defaultValue = "0.0") Double pourboire,
                               RedirectAttributes redirectAttributes) {
        // Wait a short moment to give the webhook time to process
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Commande commande = commandeService.findCommandeById(commandeId);
        if (com.example.demo.model.EtatCommande.PAYEE.equals(commande.getEtat())) {
             return "redirect:/client/paiement/succes/" + commandeId;
        } else {
             return "redirect:/client/paiement/echec/" + commandeId;
        }
    }

    @GetMapping("/succes/{commandeId}")
    public String showSuccessPage(@PathVariable Long commandeId, Model model) {
        model.addAttribute("commandeId", commandeId);
        return "client/paiement-succes";
    }

    @GetMapping("/echec/{commandeId}")
    public String showFailurePage(@PathVariable Long commandeId, Model model) {
        model.addAttribute("commandeId", commandeId);
        return "client/paiement-echec";
    }
}
