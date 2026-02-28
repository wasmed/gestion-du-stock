package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderManagementController {
    @Autowired
    private UserService userService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private PlatService platService;
    @Autowired
    private TableRestaurantRepository tableRepository;
    @Autowired
    private StockService stockService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SERVEUR', 'ADMIN')")
    public String listOrders(Model model) {
        List<TableRestaurant> toutesLesTables = tableRepository.findAll();
        model.addAttribute("toutesLesTables", toutesLesTables);

        // 2. On récupère les commandes en cours
        List<Commande> commandesEnCours = commandeService.getCommandesEnCoursEtServies();

        // 3. On groupe les commandes par leur objet TableRestaurant
        Map<TableRestaurant, List<Commande>> commandesParTable = commandesEnCours.stream()
                .filter(c -> c.getTable() != null)
                .collect(Collectors.groupingBy(Commande::getTable));

        model.addAttribute("commandesParTable", commandesParTable);

        // 4. On envoie aussi la liste des tables qui ont des commandes (pour faciliter l'affichage)
        model.addAttribute("tablesOccupees", commandesParTable.keySet());

        // 5. On récupère les commandes en attente de validation
        List<Commande> commandesAValider = commandeService.getCommandesAValider();
        model.addAttribute("commandesAValider", commandesAValider);

        // 6. On récupère les commandes prêtes à servir (PREPARATION_TERMINEE)
        List<Commande> commandesPretes = commandesEnCours.stream()
                .filter(c -> c.getEtat() == EtatCommande.PREPARATION_TERMINEE)
                .collect(Collectors.toList());
        model.addAttribute("commandesPretes", commandesPretes);

        return "serveur/dashboard";
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
        return "redirect:/orders/chef-dashboard";
    }

    @GetMapping("/serve/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String serveOrder(@PathVariable Long id) {
        Commande commande = commandeService.findCommandeById(id);

        if (commande != null && commande.getEtat() == EtatCommande.PREPARATION_TERMINEE) {
            stockService.processStockDecrementForCommande(commande);

            commande.setEtat(EtatCommande.SERVIE);
            commandeService.saveCommande(commande);
        }

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

    @GetMapping("/validate/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String validateOrder(@PathVariable Long id, Principal principal) {
        Commande commande = commandeService.findCommandeById(id);
        if (commande != null && commande.getEtat() == EtatCommande.EN_VALIDATION) {
            User serveur = userService.findUserByEmail(principal.getName());
            commande.setServeur(serveur);
            commande.setEtat(EtatCommande.EN_ATTENTE);

            TableRestaurant table = commande.getTable();
            if (table != null && table.getStatut() == StatutTable.LIBRE) {
                table.setStatut(StatutTable.OCCUPEE);
                table.setServeur(serveur);
                tableRepository.save(table);
            }
            commandeService.saveCommande(commande);
        }
        return "redirect:/orders";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('SERVEUR')")
    public String showCreateOrderForm(@RequestParam(required = false) Integer nombrePersonne,Model model) {
        List<TableRestaurant> tablesDisponibles;
        if (nombrePersonne != null && nombrePersonne > 0) {
            tablesDisponibles = tableRepository.findByStatutAndNombrePersonneGreaterThanEqual(StatutTable.LIBRE, nombrePersonne);
        } else {
            tablesDisponibles = tableRepository.findByStatut(StatutTable.LIBRE);
        }
        List<Plat> plats = platService.findAllPlats();

        Map<Boolean, List<Plat>> platsPartitionnes = plats.stream()
                .collect(Collectors.partitioningBy(plat -> plat.getCategorie() == CategoriePlat.BOISSON));

        List<Plat> boissons = platsPartitionnes.get(true);
        List<Plat> platsCuisines = platsPartitionnes.get(false);
        List<User> clients = userService.findByRole(Role.CLIENT);
        List<Menu> menus = menuService.findAllMenus();

        model.addAttribute("tables", tablesDisponibles);
        model.addAttribute("nombrePersonne", nombrePersonne);
        model.addAttribute("platsCuisines", platsCuisines); // Nouvelle variable
        model.addAttribute("boissons", boissons);
        model.addAttribute("menus", menus);
        model.addAttribute("clients", clients);
        return "serveur/formulaire-commande";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('SERVEUR')")
    public String createOrder(@RequestParam(required = false) Long tableId,
                              @RequestParam(required = false) Boolean isEmporter,
                              @RequestParam(name = "platIds", required = false) List<Long> platIds,
                              @RequestParam(name = "menuIds", required = false) List<Long> menuIds,
                              @RequestParam(name = "clientEmail", required = false) String clientEmail,
                              @RequestParam(name = "commentaire", required = false) String commentaire,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {

        if ((platIds == null || platIds.isEmpty()) && (menuIds == null || menuIds.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de créer la commande : vous devez ajouter au moins un produit.");
            return "redirect:/orders/create";
        }

        User serveur = userService.findUserByEmail(principal.getName());
        User client ;
        if (clientEmail != null && !clientEmail.trim().isEmpty()) {
            // 1. Le serveur a saisi un email, on essaie de trouver le client.
            User foundClient = userService.findUserByEmail(clientEmail);

            if (foundClient != null) {
                client = foundClient;
            } else {
                client = userService.findUserByEmail("guest@resto.com");
            }
        } else {
            client = userService.findUserByEmail("guest@resto.com");
        }

        if (client == null) {
            throw new RuntimeException("Le compte client 'guest@resto.com' est introuvable.");
        }

        if (Boolean.TRUE.equals(isEmporter)) {
            tableId = null; // Ignore table if it's takeout
        } else if (tableId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de créer la commande : aucune table sélectionnée.");
            return "redirect:/orders/create";
        }

        Commande commande;
        if (tableId != null) {
            commande = commandeService.createNewCommande(client, serveur, tableId);
        } else {
            commande = commandeService.createNewCommande(client, serveur);
        }
        if (Boolean.TRUE.equals(isEmporter)) {
            commande.setIsEmporter(true);
        }

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

        TableRestaurant table = null;
        if (tableId != null) {
            table = tableRepository.findById(tableId).orElse(null);
            if (table != null) {
                // ON MET LA TABLE EN STATUT OCCUPÉE
                table.setStatut(StatutTable.OCCUPEE);
                table.setServeur(serveur);
                tableRepository.save(table);
            }
        }
        // Mise à jour du montant total de la commande et sauvegarde
        commande.setMontantTotal(montantTotal);
        commande.setTable(table);
        commande.setCommentaire(commentaire);
        commandeService.saveCommande(commande);

        return "redirect:/orders";
    }

    @GetMapping("/pay/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String payOrder(@PathVariable Long id) {
        return "redirect:/paiement/form/" + id;
    }

    @GetMapping("/finish-preparation/{id}")
    @PreAuthorize("hasRole('CHEF_CUISINIER')")
    public String finishPreparation(@PathVariable Long id) {
        commandeService.updateCommandeEtat(id, EtatCommande.PREPARATION_TERMINEE);
        // On redirige le chef vers son propre tableau de bord
        return "redirect:/orders/chef-dashboard";
    }
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String showEditOrderForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Commande commande = commandeService.findCommandeById(id);

        if (commande.getEtat() != EtatCommande.EN_ATTENTE && commande.getEtat() != EtatCommande.EN_PREPARATION && commande.getEtat() != EtatCommande.SERVIE) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de modifier cette commande (elle doit être EN_ATTENTE, EN_PREPARATION, ou SERVIE).");
            return "redirect:/orders";
        }

        // --- LOGIQUE MISE À JOUR POUR PRÉPARER TOUTES LES LISTES ---
        List<Plat> tousLesPlats = platService.findAllPlats();
        Map<Boolean, List<Plat>> platsPartitionnes = tousLesPlats.stream()
                .collect(Collectors.partitioningBy(plat -> plat.getCategorie() == CategoriePlat.BOISSON));

        List<Plat> boissons = platsPartitionnes.get(true);
        List<Plat> platsCuisines = platsPartitionnes.get(false);

        model.addAttribute("commande", commande);
        model.addAttribute("tables", tableRepository.findAll());
        model.addAttribute("platsCuisines", platsCuisines); // Envoie la liste de plats cuisinés
        model.addAttribute("boissons", boissons);           // Envoie la liste de boissons
        model.addAttribute("menus", menuService.findAllMenus()); // Envoie la liste des menus

        return "serveur/formulaire-modification";
    }
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String editOrder(@PathVariable Long id,
                            @RequestParam Long tableId,
                            @RequestParam(name = "platIds", required = false) List<Long> platIds,
                            @RequestParam(name = "menuIds", required = false) List<Long> menuIds,
                            Principal principal) {

        Commande commande = commandeService.findCommandeById(id);
        double montantTotal = commande.getMontantTotal();

        // Si la commande est déjà en préparation ou servie, on force l'état en EN_ATTENTE pour les nouveaux ajouts
        // et on ne supprime PAS les anciennes lignes
        if (commande.getEtat() == EtatCommande.EN_PREPARATION || commande.getEtat() == EtatCommande.SERVIE || commande.getEtat() == EtatCommande.PREPARATION_TERMINEE) {
            commande.setEtat(EtatCommande.EN_ATTENTE);
        } else {
            // Si elle est juste EN_ATTENTE, on remplace tout (comportement d'origine)
            montantTotal = 0;
            ligneCommandeRepository.deleteAll(commande.getLignesCommande());
            commande.getLignesCommande().clear();
        }

        // 2. Ajouter les nouvelles lignes de commande
        if (platIds != null) {
            for (Long platId : platIds) {
                Plat plat = platService.findPlatById(platId);
                LigneCommande ligne = new LigneCommande();
                ligne.setCommande(commande);
                ligne.setPlat(plat);
                ligne.setQuantite(1); // Gérer la quantité si nécessaire
                ligne.setTypeLigne(TypeLigneCommande.PLAT);
                montantTotal += plat.getPrix();
                ligneCommandeRepository.save(ligne);
                commande.getLignesCommande().add(ligne); // <-- LA LIGNE CLÉ À AJOUTER
            }
        }

        if (menuIds != null) {
            for (Long menuId : menuIds) {
                Menu menu = menuService.findMenuById(menuId);
                LigneCommande ligne = new LigneCommande();
                ligne.setCommande(commande);
                ligne.setMenu(menu);
                ligne.setQuantite(1); // Gérer la quantité si nécessaire
                ligne.setTypeLigne(TypeLigneCommande.MENU);
                montantTotal += menu.getPrix();
                ligneCommandeRepository.save(ligne);
                commande.getLignesCommande().add(ligne); // <-- LA LIGNE CLÉ À AJOUTER
            }
        }

        // 3. Mettre à jour la table
        // Attention : la méthode est peut-être findById et non findByIdentifiant
        TableRestaurant table = tableRepository.findById(tableId).orElse(null);
        if (table != null) {
            commande.setTable(table);
        }

        commande.setMontantTotal(montantTotal);
        // 4. Sauvegarder la commande mise à jour
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
