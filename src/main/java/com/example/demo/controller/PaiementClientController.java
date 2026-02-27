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
@RequestMapping("/client/paiement/simulation")
public class PaiementClientController {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PaiementService paiementService;

    @GetMapping("/{commandeId}")
    public String showSimulationPage(@PathVariable Long commandeId, Model model) {
        Commande commande = commandeService.findCommandeById(commandeId);
        model.addAttribute("commande", commande);
        return "client/paiement-simulation";
    }

    @PostMapping("/process")
    public String processSimulationPayment(@RequestParam Long commandeId,
                                           RedirectAttributes redirectAttributes) {
        // Simuler un paiement par QR Code (qui est en fait un paiement externe validé)
        // Ici on suppose que le client a "payé" via l'interface de simulation.
        // On enregistre le paiement avec un montant de pourboire à 0 par défaut pour la simulation.
        paiementService.processPayment(commandeId, 0.0, ModePaiement.QR_CODE);

        redirectAttributes.addFlashAttribute("successMessage", "Paiement simulé validé avec succès !");
        return "redirect:/client/paiement/simulation/" + commandeId + "?success";
    }
}
