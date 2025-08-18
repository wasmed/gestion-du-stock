package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.service.CommandeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderManagementController {

    @Autowired
    private CommandeService commandeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SERVEUR', 'CHEF_CUISINIER', 'ADMIN')")
    public String listOrders(Model model) {
        // Logique pour afficher les commandes
        List<Commande> commandesEnCours = commandeService.getCommandesEnCours();
        model.addAttribute("commandes", commandesEnCours);
        return "orders/list"; // Renvoie vers orders/list.html
    }

    @GetMapping("/prepare/{id}")
    @PreAuthorize("hasRole('CHEF_CUISINIER')")
    public String prepareOrder(@PathVariable Long id) {
        commandeService.updateCommandeEtat(id, EtatCommande.EN_PREPARATION);
        return "redirect:/orders";
    }

    @GetMapping("/serve/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String serveOrder(@PathVariable Long id) {
        commandeService.updateCommandeEtat(id, EtatCommande.SERVIE);
        return "redirect:/orders";
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasAnyRole('SERVEUR', 'CHEF_CUISINIER', 'ADMIN')")
    public String showOrderDetails(@PathVariable Long id, Model model) {
        Commande commande = commandeService.findCommandeById(id); // Ajoute cette méthode dans CommandeService
        model.addAttribute("commande", commande);
        return "orders/details"; // Renvoie vers orders/details.html
    }
}
