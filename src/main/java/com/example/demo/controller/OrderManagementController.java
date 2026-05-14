package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public String listOrders(Model model, Principal principal) {
        User currentUser = userService.findUserByEmail(principal.getName());
        model.addAttribute("currentUser", currentUser);
        List<TableRestaurant> toutesLesTables = tableRepository.findAll();
        model.addAttribute("toutesLesTables", toutesLesTables);

        // 2. On récupère les commandes en cours
        List<Commande> commandesEnCours = commandeService.getCommandesEnCoursEtServies();

        model.addAttribute("toutesLesCommandes", commandesEnCours);

        // 3. On groupe les commandes par leur objet TableRestaurant
        Map<TableRestaurant, List<Commande>> commandesParTable = commandesEnCours.stream()
                .filter(c -> c.getTable() != null && !Boolean.TRUE.equals(c.getIsEmporter()))
                .collect(Collectors.groupingBy(Commande::getTable));

        model.addAttribute("commandesParTable", commandesParTable);

        // 4. On envoie aussi la liste des tables qui ont des commandes (pour faciliter l'affichage)
        model.addAttribute("tablesOccupees", commandesParTable.keySet());

        // 4bis. On récupère les commandes à emporter en cours
        List<Commande> commandesAEmporter = commandesEnCours.stream()
                .filter(c -> c.getTable() == null || Boolean.TRUE.equals(c.getIsEmporter()))
                .collect(Collectors.toList());
        model.addAttribute("commandesAEmporter", commandesAEmporter);

        // 5. On récupère les commandes en attente de validation
        List<Commande> commandesAValider = commandeService.getCommandesAValider();
        model.addAttribute("commandesAValider", commandesAValider);

        // 6. On récupère les commandes prêtes à servir (Toutes les lignes SERVIE)
        List<Commande> commandesPretes = commandesEnCours.stream()
                .filter(Commande::isPreteAServir)
                .collect(Collectors.toList());
        model.addAttribute("commandesPretes", commandesPretes);

        model.addAttribute("tablesDisponibles", tableRepository.findAvailableTablesNotInActiveOrders());

        return "serveur/dashboard";
    }


    @GetMapping("/details/{id}")
    @PreAuthorize("hasAnyRole('SERVEUR', 'CHEF_CUISINIER', 'ADMIN')")
    public String showOrderDetails(@PathVariable Long id, Model model) {
        Commande commande = commandeService.findCommandeById(id);
        model.addAttribute("commande", commande);
        return "orders/details";
    }

    @GetMapping("/prepare-ligne/{id}")
    @PreAuthorize("hasRole('CHEF_CUISINIER')")
    public String prepareLigne(@PathVariable Long id) {
        commandeService.updateLigneCommandeEtat(id, EtatLigneCommande.EN_PREPARATION);
        return "redirect:/orders/chef-dashboard";
    }

    @GetMapping("/finish-preparation-ligne/{id}")
    @PreAuthorize("hasRole('CHEF_CUISINIER')")
    public String finishPreparationLigne(@PathVariable Long id) {
        commandeService.updateLigneCommandeEtat(id, EtatLigneCommande.PREPARATION_TERMINEE);
        return "redirect:/orders/chef-dashboard";
    }

    @GetMapping("/serve/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String serveOrder(@PathVariable Long id) {
        Commande commande = commandeService.findCommandeById(id);
        if (commande != null && commande.getLignesCommande() != null) {
            boolean hasServed = false;
            for (LigneCommande ligne : commande.getLignesCommande()) {
                if (ligne.getEtat() == EtatLigneCommande.PREPARATION_TERMINEE) {
                    ligne.setEtat(EtatLigneCommande.SERVIE);
                    ligneCommandeRepository.save(ligne);
                    hasServed = true;
                }
            }
        }
        return "redirect:/orders";
    }

    @GetMapping("/chef-dashboard")
    @PreAuthorize("hasRole('CHEF_CUISINIER')")
    public String chefDashboard(Model model) {
        List<Commande> commandesEnPreparation = commandeService.getCommandesEnAttenteEtEnPreparation();
        model.addAttribute("commandes", commandesEnPreparation);

        List<StockProduit> lowStocks = stockService.findLowStocks();
        model.addAttribute("lowStocks", lowStocks);

        return "orders/chef-dashboard";
    }

    // --- Création de commande par le SERVEUR ---

    @GetMapping("/validate-lines/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String validateOrderLines(@PathVariable Long id, Principal principal) {
        Commande commande = commandeService.findCommandeById(id);
        if (commande != null) {
            boolean hasLinesValidated = false;
            if (commande.getLignesCommande() != null) {
                for (LigneCommande ligne : commande.getLignesCommande()) {
                    if (ligne.getEtat() == EtatLigneCommande.EN_VALIDATION) {
                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                        ligneCommandeRepository.save(ligne);
                        stockService.processStockDecrementForLigne(ligne);
                        hasLinesValidated = true;
                    }
                }
            }

            if (hasLinesValidated) {
                // S'assurer que la commande est au minimum EN_COURS ou la repasser en EN_COURS
                // pour signaler à la cuisine qu'il y a de nouvelles lignes EN_ATTENTE
                if (commande.getEtat() == EtatCommande.PAYEE) {
                    // Normalement impossible, mais au cas où
                } else {
                    commande.setEtat(EtatCommande.EN_COURS);
                }
                commandeService.saveCommande(commande);
            }
        }
        return "redirect:/orders";
    }

    @RequestMapping(value = "/validate/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    @PreAuthorize("hasRole('SERVEUR')")
    public String validateOrder(@PathVariable Long id, @RequestParam(required = false) Long tableId, Principal principal, RedirectAttributes redirectAttributes) {
        Commande commande = commandeService.findCommandeById(id);
        if (commande != null && commande.getEtat() == EtatCommande.EN_VALIDATION) {
            User serveur = userService.findUserByEmail(principal.getName());
            commande.setServeur(serveur);
            commande.setEtat(EtatCommande.EN_COURS);

            if (!Boolean.TRUE.equals(commande.getIsEmporter()) && tableId != null) {
                TableRestaurant table = tableRepository.findById(tableId).orElse(null);
                if (table != null) {
                    List<TableRestaurant> availableTables = tableRepository.findAvailableTablesNotInActiveOrders();
                    boolean isAvailable = availableTables.stream().anyMatch(t -> t.getIdentifiant().equals(table.getIdentifiant()));
                    if (isAvailable) {
                        table.setStatut(StatutTable.OCCUPEE);
                        table.setServeur(serveur);
                        tableRepository.save(table);
                        commande.setTable(table);
                    } else {
                        redirectAttributes.addFlashAttribute("errorMessage", "La table sélectionnée n'est pas disponible.");
                        return "redirect:/orders";
                    }
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "La table sélectionnée n'est pas disponible.");
                    return "redirect:/orders";
                }
            }
            commandeService.saveCommande(commande);

            // Decrement stock for all lines since they are now validated and sent to kitchen
            if (commande.getLignesCommande() != null) {
                for (LigneCommande ligne : commande.getLignesCommande()) {
                    if (ligne.getEtat() == EtatLigneCommande.EN_VALIDATION) {
                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                        ligneCommandeRepository.save(ligne);
                        stockService.processStockDecrementForLigne(ligne);
                    }
                }
            }
        }
        return "redirect:/orders";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('SERVEUR')")
    public String showCreateOrderForm(@RequestParam(required = false) Integer nombrePersonne,
                                      @RequestParam(required = false) Long tableId,
                                      Model model) {
        List<TableRestaurant> tablesDisponibles;
        if (nombrePersonne != null && nombrePersonne > 0) {
            tablesDisponibles = tableRepository.findAvailableTablesNotInActiveOrdersWithCapacity(nombrePersonne);
        } else {
            tablesDisponibles = tableRepository.findAvailableTablesNotInActiveOrders();
        }

        if (tableId != null) {
            TableRestaurant table = tableRepository.findById(tableId).orElse(null);
            if (table != null) {
                boolean isAvailable = tablesDisponibles.stream().anyMatch(t -> t.getIdentifiant().equals(table.getIdentifiant()));
                if (isAvailable) {
                    model.addAttribute("selectedTable", table);
                }
            }
        }

        List<Plat> plats = platService.findAllActivePlats();

        Map<Boolean, List<Plat>> platsPartitionnes = plats.stream()
                .collect(Collectors.partitioningBy(plat -> plat.getCategorie() == CategoriePlat.BOISSON));

        List<Plat> boissons = platsPartitionnes.get(true);
        List<Plat> platsCuisines = platsPartitionnes.get(false);
        List<User> clients = userService.findByRole(Role.CLIENT);
        List<Menu> menus = menuService.findAllActiveMenus();

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
                              @RequestParam Map<String, String> allParams,
                              @RequestParam(name = "clientEmail", required = false) String clientEmail,
                              @RequestParam(name = "commentaire", required = false) String commentaire,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {

        boolean hasItems = false;
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            if ((entry.getKey().startsWith("platQty_") || entry.getKey().startsWith("menuQty_")) && !entry.getValue().isEmpty()) {
                try {
                    if (Integer.parseInt(entry.getValue()) > 0) {
                        hasItems = true;
                        break;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
            }
        }

        if (!hasItems) {
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

        // Ajout des plats et menus à la commande
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty()) continue;

            try {
                int quantite = Integer.parseInt(value);
                if (quantite > 0) {
                    if (key.startsWith("platQty_")) {
                        Long platId = Long.parseLong(key.substring(8));
                        Plat plat = platService.findPlatById(platId);
                        if (plat != null) {
                            commandeService.addPlatToCommande(commande, platId, quantite); // Utilise la méthode du service
                            montantTotal += plat.getPrix() * quantite;
                        }
                    } else if (key.startsWith("menuQty_")) {
                        Long menuId = Long.parseLong(key.substring(8));
                        Menu menu = menuService.findMenuById(menuId);
                        if (menu != null) {
                            commandeService.addMenuToCommande(commande, menuId, quantite); // Utilise la méthode du service
                            montantTotal += menu.getPrix() * quantite;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
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

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String showEditOrderForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Commande commande = commandeService.findCommandeById(id);

        if (commande.getEtat() != EtatCommande.EN_COURS) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible de modifier cette commande (elle doit être EN_COURS).");
            return "redirect:/orders";
        }

        // --- LOGIQUE MISE À JOUR POUR PRÉPARER TOUTES LES LISTES ---
        List<Plat> tousLesPlats = platService.findAllActivePlats();
        Map<Boolean, List<Plat>> platsPartitionnes = tousLesPlats.stream()
                .collect(Collectors.partitioningBy(plat -> plat.getCategorie() == CategoriePlat.BOISSON));

        List<Plat> boissons = platsPartitionnes.get(true);
        List<Plat> platsCuisines = platsPartitionnes.get(false);

        model.addAttribute("commande", commande);
        model.addAttribute("tables", tableRepository.findAll());
        model.addAttribute("platsCuisines", platsCuisines); // Envoie la liste de plats cuisinés
        model.addAttribute("boissons", boissons);           // Envoie la liste de boissons
        model.addAttribute("menus", menuService.findAllActiveMenus()); // Envoie la liste des menus

        return "serveur/formulaire-modification";
    }
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('SERVEUR')")
    public String editOrder(@PathVariable Long id,
                            @RequestParam Long tableId,
                            @RequestParam Map<String, String> allParams,
                            Principal principal) {

        Commande commande = commandeService.findCommandeById(id);
        double montantTotal = commande.getMontantTotal();

        // On ne supprime JAMAIS les anciennes lignes, on en ajoute simplement de nouvelles
        // car l'état de préparation est par ligne.
        // Si on veut permettre la suppression, il faut le faire via une autre interface.

        // 2. Ajouter les nouvelles lignes de commande
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.isEmpty()) continue;

            try {
                int quantite = Integer.parseInt(value);
                if (quantite > 0) {
                    if (key.startsWith("platQty_")) {
                        Long platId = Long.parseLong(key.substring(8));
                        Plat plat = platService.findPlatById(platId);
                        if (plat != null) {
                            Optional<LigneCommande> existingLigne = commande.getLignesCommande().stream()
                                    .filter(l -> l.getPlat() != null && l.getPlat().getIdentifiant().equals(plat.getIdentifiant()) && l.getEtat() == EtatLigneCommande.EN_ATTENTE)
                                    .findFirst();

                            LigneCommande ligne;
                            if (existingLigne.isPresent()) {
                                ligne = existingLigne.get();
                                ligne.setQuantite(ligne.getQuantite() + quantite);
                            } else {
                                ligne = new LigneCommande();
                                ligne.setCommande(commande);
                                ligne.setPlat(plat);
                                ligne.setQuantite(quantite);
                                ligne.setTypeLigne(TypeLigneCommande.PLAT);
                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                                commande.getLignesCommande().add(ligne);
                            }
                            montantTotal += plat.getPrix() * quantite;
                            ligne = ligneCommandeRepository.save(ligne);

                            LigneCommande deltaLigne = new LigneCommande();
                            deltaLigne.setPlat(plat);
                            deltaLigne.setQuantite(quantite);
                            stockService.processStockDecrementForLigne(deltaLigne);
                        }
                    } else if (key.startsWith("menuQty_")) {
                        Long menuId = Long.parseLong(key.substring(8));
                        Menu menu = menuService.findMenuById(menuId);
                        if (menu != null) {
                            Optional<LigneCommande> existingLigne = commande.getLignesCommande().stream()
                                    .filter(l -> l.getMenu() != null && l.getMenu().getId().equals(menu.getId()) && l.getEtat() == EtatLigneCommande.EN_ATTENTE)
                                    .findFirst();

                            LigneCommande ligne;
                            if (existingLigne.isPresent()) {
                                ligne = existingLigne.get();
                                ligne.setQuantite(ligne.getQuantite() + quantite);
                            } else {
                                ligne = new LigneCommande();
                                ligne.setCommande(commande);
                                ligne.setMenu(menu);
                                ligne.setQuantite(quantite);
                                ligne.setTypeLigne(TypeLigneCommande.MENU);
                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                                commande.getLignesCommande().add(ligne);
                            }
                            montantTotal += menu.getPrix() * quantite;
                            ligne = ligneCommandeRepository.save(ligne);

                            LigneCommande deltaLigne = new LigneCommande();
                            deltaLigne.setMenu(menu);
                            deltaLigne.setQuantite(quantite);
                            stockService.processStockDecrementForLigne(deltaLigne);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore invalid numbers
            }
        }

        // 3. Mettre à jour la table
        // Attention : la méthode est peut-être findById et non findByIdentifiant
        TableRestaurant table = tableRepository.findById(tableId).orElse(null);
        if (table != null) {
            TableRestaurant oldTable = commande.getTable();
            if (oldTable != null && !oldTable.getIdentifiant().equals(table.getIdentifiant())) {
                // Libérer l'ancienne table
                oldTable.setStatut(StatutTable.LIBRE);
                oldTable.setServeur(null);
                tableRepository.save(oldTable);
            }

            if (oldTable == null || !oldTable.getIdentifiant().equals(table.getIdentifiant())) {
                // Occuper la nouvelle table
                table.setStatut(StatutTable.OCCUPEE);
                User serveur = userService.findUserByEmail(principal.getName());
                table.setServeur(serveur);
                tableRepository.save(table);
            }
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
