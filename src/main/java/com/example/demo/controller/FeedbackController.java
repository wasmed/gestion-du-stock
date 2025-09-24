package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.model.Feedback;
import com.example.demo.service.CommandeService;
import com.example.demo.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private CommandeService commandeService;


    @GetMapping("/laisser-avis")
    @PreAuthorize("hasRole('CLIENT')")
    public String showFeedbackForm(@RequestParam Long commandeId, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        Commande commande = commandeService.findCommandeById(commandeId);

        // Vérifier que la commande appartient bien au client connecté
        if (!commande.getClient().getEmail().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("error", "Accès non autorisé à cette commande.");
            // --- CORRECTION 1 ---
            return "redirect:/client/historique"; // Le chemin correct est /client/historique
        }

        // Vérifier que la commande n'a pas déjà un feedback
        if (commande.getFeedBack() != null) {
            redirectAttributes.addFlashAttribute("error", "Vous avez déjà laissé un avis pour cette commande.");
            // --- CORRECTION 2 ---
            return "redirect:/client/historique";
        }

        // Vérifier que la commande est bien dans un état "final"
        if (commande.getEtat() != EtatCommande.PAYEE) {
            redirectAttributes.addFlashAttribute("error", "Vous ne pouvez laisser un avis que pour une commande payée.");
            // --- CORRECTION 3 ---
            return "redirect:/client/historique";
        }

        model.addAttribute("feedback", new Feedback());
        model.addAttribute("commande", commande);
        return "feedback/formulaire";
    }

    @PostMapping("/sauvegarder")
    @PreAuthorize("hasRole('CLIENT')")
    public String submitFeedback(@RequestParam Long commandeId,
                                 @RequestParam int note,
                                 @RequestParam String commentaire,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        Commande commande = commandeService.findCommandeById(commandeId);
        if (!commande.getClient().getEmail().equals(principal.getName())) {
            return "redirect:/error";
        }

        try {
            feedbackService.createFeedback(commande, note, commentaire);
            redirectAttributes.addFlashAttribute("success", "Merci ! Votre avis a bien été enregistré.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // --- CORRECTION PRINCIPALE ---
        return "redirect:/client/historique"; // On redirige vers /client/historique
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.findAllFeedbacks();
        model.addAttribute("feedbacks", feedbacks);
        return "feedback/list";
    }
}
