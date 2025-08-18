package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.LigneCommande;
import com.example.demo.model.Plat;
import com.example.demo.model.User;
import com.example.demo.service.CommandeService;
import com.example.demo.service.PlatService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/client")
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {

    @Autowired
    private PlatService platService;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private UserService userService;

    @GetMapping("/menu")
    public String showMenu(Model model) {
        List<Plat> plats = platService.findAllPlats();
        model.addAttribute("plats", plats);
        return "client/menu"; // Renvoie vers client/menu.html
    }

    @GetMapping("/add-to-cart")
    public String addPlatToCart(@RequestParam Long platId, HttpSession session) {
        // Logique simplifiée : ajouter le plat au panier en session
        Plat plat = platService.findPlatById(platId);
        List<Plat> cart = (List<Plat>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        cart.add(plat);
        session.setAttribute("cart", cart);

        return "redirect:/client/menu"; // Redirige vers le menu
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        List<Plat> cart = (List<Plat>) session.getAttribute("cart");
        model.addAttribute("cart", cart != null ? cart : new ArrayList<>());
        return "client/cart"; // Renvoie vers client/cart.html
    }

    @PostMapping("/checkout")
    public String checkout(HttpSession session,Principal principal) {
        // Récupérer le panier de la session
        List<Plat> cart = (List<Plat>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            return "redirect:/client/menu";
        }
// Récupérer le client connecté via son email
        String clientEmail = principal.getName();
        User client = userService.findUserByEmail(clientEmail);
        // Créer une commande vide
        // On suppose que le serveur est un utilisateur de test créé en dur
        User serveur = userService.findUserByEmail("serveur@restaurant.com"); // À implémenter
// Créer une nouvelle commande
        Commande commande = commandeService.createNewCommande(client, serveur);
        // Créer les lignes de commande à partir du panier
        for (Plat plat : cart) {
            LigneCommande ligne = new LigneCommande();
            ligne.setCommande(commande);
            ligne.setPlat(plat);
            ligne.setQuantite(1); // On met une quantité de 1 pour simplifier
            commandeService.saveLigneCommande(ligne); // Ajoute cette méthode dans CommandeService
        }

        // Calculer le montant total de la commande
        double montantTotal = cart.stream().mapToDouble(Plat::getPrix).sum();
        commande.setMontantTotal(montantTotal);
        commandeService.saveCommande(commande);

        // Vider le panier
        session.removeAttribute("cart");

        return "redirect:/client/confirmation"; // Ou vers une page de confirmation
    }
}
