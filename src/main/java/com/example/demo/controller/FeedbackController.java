package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.model.Feedback;
import com.example.demo.model.User;
import com.example.demo.service.CommandeService;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private UserService userService;

    @GetMapping("/laisser-avis")
    @PreAuthorize("hasRole('CLIENT')")
    public String showFeedbackForm(@RequestParam Long commandeId, Model model, Principal principal, RedirectAttributes redirectAttributes) {

        Commande commande = commandeService.findCommandeById(commandeId);

        // --- SÉCURITÉ ET VÉRIFICATIONS IMPORTANTES ---
        // 1. Vérifier que la commande appartient bien au client connecté
        if (!commande.getClient().getEmail().equals(principal.getName())) {
            redirectAttributes.addFlashAttribute("error", "Accès non autorisé à cette commande.");
            return "redirect:/historique"; // On le renvoie à son historique
        }

        // 2. Vérifier que la commande n'a pas déjà un feedback
        if (commande.getFeedBack() != null) {
            redirectAttributes.addFlashAttribute("error", "Vous avez déjà laissé un avis pour cette commande.");
            return "redirect:/historique";
        }

        // 3. (Optionnel) Vérifier que la commande est bien dans un état "final" (ex: PAYEE)
        if (commande.getEtat() != EtatCommande.PAYEE) {
            redirectAttributes.addFlashAttribute("error", "Vous ne pouvez laisser un avis que pour une commande payée.");
            return "redirect:/historique";
        }

        // Si tout est bon, on prépare le modèle pour la vue
        model.addAttribute("feedback", new Feedback());
        model.addAttribute("commande", commande);
        return "feedback/formulaire"; // Le chemin vers ta nouvelle vue
    }

    // L'ANCIENNE MÉTHODE POST /create EST REMPLACÉE PAR CELLE-CI :
    /**
     * Traite la soumission du formulaire de feedback.
     */
    @PostMapping("/sauvegarder")
    @PreAuthorize("hasRole('CLIENT')")
    public String submitFeedback(@RequestParam Long commandeId,
                                 @RequestParam int note,
                                 @RequestParam String commentaire,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        // On refait les vérifications de sécurité par précaution
        Commande commande = commandeService.findCommandeById(commandeId);
        if (!commande.getClient().getEmail().equals(principal.getName())) {
            return "redirect:/error"; // Page d'erreur générique
        }

        try {
            feedbackService.createFeedback(commande, note, commentaire);
            redirectAttributes.addFlashAttribute("success", "Merci ! Votre avis a bien été enregistré.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/historique"; // Redirige vers l'historique avec un message de succès/erreur
    }
    // Show the list of feedbacks for admins
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.findAllFeedbacks();
        model.addAttribute("feedbacks", feedbacks);
        return "feedback/list";
    }
}
