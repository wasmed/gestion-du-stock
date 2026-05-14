package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.Paiement;
import com.example.demo.repository.PaiementRepository;
import com.example.demo.service.CommandeService;
import com.example.demo.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/client/facture")
public class FactureClientController {

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/{id}")
    public String showFacture(@PathVariable Long id, Model model) {
        Commande commande = commandeService.findCommandeById(id);

        Paiement paiement = paiementRepository.findByCommandeId(id).orElse(null);

        double total = commande.getMontantTotal();
        // Assuming a standard VAT of 12% for the example
        double tva = total * 0.12;
        double subtotal = total - tva;

        model.addAttribute("commande", commande);
        model.addAttribute("paiement", paiement);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tva", tva);

        return "client/facture";
    }

    @PostMapping("/send-email")
    public String sendFactureEmail(@RequestParam String email, @RequestParam Long commandeId, RedirectAttributes redirectAttributes) {

        try {
            Commande commande = commandeService.findCommandeById(commandeId);
            emailService.sendInvoiceEmail(email, commande);
            redirectAttributes.addFlashAttribute("successMessage", "La facture a été envoyée avec succès à " + email);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'envoi de la facture.");
        }

        return "redirect:/client/facture/" + commandeId;
    }
}
