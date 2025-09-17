package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.ModePaiement;
import com.example.demo.service.CommandeService;
import com.example.demo.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/paiement")
@PreAuthorize("hasRole('SERVEUR')") // Seul le serveur peut gérer les paiements
public class PaiementController {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PaiementService paiementService;

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
}
