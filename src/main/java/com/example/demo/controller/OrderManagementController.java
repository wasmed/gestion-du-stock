package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.TableRestaurantRepository;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderManagementController {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private PlatService platService;
    @Autowired
    private TableRestaurantRepository tableRepository;
    @Autowired
    private StockService stockService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SERVEUR', 'CHEF_CUISINIER', 'ADMIN')")
    public String listOrders(Model model) {
        List<Commande> commandesEnCours = commandeService.getCommandesEnCoursEtServies();
        model.addAttribute("commandes", commandesEnCours);
        return "orders/list";
    }

    @GetMapping("/details/{id}")
    @PreAuthorize("hasAnyRole('SERVEUR', 'CHEF_CUISINIER', 'ADMIN')")
    public String showOrderDetails(@PathVariable Long id, Model model) {
        Commande commande = commandeService.findCommandeById(id);
        model.addAttribute("commande", commande);
        return "orders/details";
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
        Commande commande = commandeService.findCommandeById(id);
        stockService.decrementStockForCommande(commande);
        commande.setEtat(EtatCommande.SERVIE);
        commandeService.saveCommande(commande);

        return "redirect:/orders";
    }

    @GetMapping("/chef-dashboard")
    @PreAuthorize("hasRole('CHEF_CUISINIER')")
    public String chefDashboard(Model model) {
        List<Commande> commandesEnPreparation = commandeService.getCommandesEnAttenteEtEnPreparation();
        model.addAttribute("commandes", commandesEnPreparation);
        return "orders/chef-dashboard";
    }

    // --- Création de commande par le SERVEUR ---

    @GetMapping("/create")
    @PreAuthorize("hasRole('SERVEUR')")
    public String showCreateOrderForm(Model model) {
        List<TableRestaurant> tables = tableRepository.findAll();
        List<Plat> plats = platService.findAllPlats();
        List<User> clients = userService.findByRole(Role.CLIENT);
        model.addAttribute("tables", tables);
        model.addAttribute("plats", plats);
        model.addAttribute("clients", clients);
        return "orders/create";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('SERVEUR')")
    public String createOrder(@RequestParam Long tableId,
                              @RequestParam(name = "platIds", required = false) List<Long> platIds,
                              @RequestParam(name = "menuIds", required = false) List<Long> menuIds,
                              @RequestParam Long clientId,
                              Principal principal) {
        User serveur = userService.findUserByEmail(principal.getName());
        User client = userService.findUserById(clientId);
        Commande commande = commandeService.createNewCommande(client, serveur, tableId);

        double montantTotal = 0;

        // Ajout des plats à la commande
        if (platIds != null && !platIds.isEmpty()) {
            for (Long platId : platIds) {
                Plat plat = platService.findPlatById(platId);
                if (plat != null) {
                    commandeService.addPlatToCommande(commande, platId, 1); // Utilise la méthode du service
                    montantTotal += plat.getPrix();
                }
            }
        }

        // Ajout des menus à la commande
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                Menu menu = menuService.findMenuById(menuId);
                if (menu != null) {
                    commandeService.addMenuToCommande(commande, menuId, 1); // Utilise la méthode du service
                    montantTotal += menu.getPrix();
                }
            }
        }

        // Mise à jour du montant total de la commande et sauvegarde
        commande.setMontantTotal(montantTotal);
        commandeService.saveCommande(commande);

        return "redirect:/orders";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String deleteOrder(@PathVariable Long id) {
        commandeService.deleteOrder(id);
        return "redirect:/orders";
    }
}
