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
                                           RedirectAttributes redirectAttributes) {
        // Simuler un paiement par QR Code (qui est en fait un paiement externe validé)
        // Ici on suppose que le client a "payé" via l'interface de simulation.
        // On enregistre le paiement avec le montant de pourboire.
        paiementService.processPayment(commandeId, pourboire, ModePaiement.QR_CODE);

        redirectAttributes.addFlashAttribute("successMessage", "Paiement simulé validé avec succès !");
        return "redirect:/client/paiement/simulation/" + commandeId + "?success";
    }

    @GetMapping("/mollie-return/{commandeId}")
    public String mollieReturn(@PathVariable Long commandeId,
                               @RequestParam(required = false, defaultValue = "0.0") Double pourboire,
                               RedirectAttributes redirectAttributes) {
        // Normalement on récupère le payment_id via session/cache ou webhook,
        // ici pour un mode test ou sans webhook configuré, on simule la vérification.
        // (La bonne pratique est d'utiliser le webhook de Mollie)

        // Simuler le fait que le paiement est OK si on est redirigé ici dans le cadre de ce test local
        // Dans la vraie vie, on vérifierait le statut du paiement avec l'ID Mollie.
        boolean isPaid = true;

        if (isPaid) {
             paiementService.processPayment(commandeId, pourboire, ModePaiement.QR_CODE);
             return "redirect:/client/paiement/simulation/" + commandeId + "?success";
        } else {
             redirectAttributes.addFlashAttribute("errorMessage", "Le paiement a été annulé ou a échoué.");
             return "redirect:/client/paiement/simulation/" + commandeId + "?error=mollie_failed";
        }
    }
}
