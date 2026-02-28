package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.TableRestaurantRepository;
import com.example.demo.service.CommandeService;
import com.example.demo.service.MenuService;
import com.example.demo.service.PlatService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;
import java.security.Principal;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private PlatService platService;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;
    @Autowired
    private TableRestaurantRepository tableRepository;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('CLIENT')")
    public String showClientDashboard(Model model, Principal principal) {
        if ("guest@resto.com".equals(principal.getName())) {
            model.addAttribute("isGuest", true);
            model.addAttribute("dernieresCommandes", Collections.emptyList());
            return "client/dashboard";
        }
        // Pour les clients normaux
        model.addAttribute("isGuest", false);
        User client = userService.findUserByEmail(principal.getName());
        model.addAttribute("client", client);
        Optional<Commande> commandeActiveOpt = commandeRepository.findActiveCommandeByClientId(client.getId());
        commandeActiveOpt.ifPresent(commande -> model.addAttribute("commandeEnCours", commande));
        List<Commande> dernieresCommandes = commandeRepository.findByClientIdOrderByDateHeureDesc(client.getId(), PageRequest.of(0, 3));
        model.addAttribute("dernieresCommandes", dernieresCommandes);

        return "client/dashboard";
    }

    @GetMapping("/menu")
    public String showMenu(Model model) {
        List<Plat> plats = platService.findAllPlats();
        List<Menu> menus = menuService.findAllMenus();
        Map<CategoriePlat, List<Plat>> platsParCategorie = plats.stream()
                .collect(Collectors.groupingBy(Plat::getCategorie, LinkedHashMap::new, Collectors.toList()));

        model.addAttribute("platsParCategorie", platsParCategorie);
        model.addAttribute("menus", menus);
        return "client/menu"; // Renvoie vers client/menu.html
    }

    @GetMapping("/add-to-cart")
    @PreAuthorize("hasRole('CLIENT')")
    @SuppressWarnings("unchecked")
    public String addToCart(@RequestParam(required = false)Long platId,
                            @RequestParam(required = false) Long menuId, HttpSession session, RedirectAttributes redirectAttributes) {
        String addedItemName = "";

        // Gestion des plats
        List<Plat> cartPlats = (List<Plat>) session.getAttribute("cartPlats");
        if (cartPlats == null) {
            cartPlats = new ArrayList<>();
        }

        // Gestion des menus
        List<Menu> cartMenus = (List<Menu>) session.getAttribute("cartMenus");
        if (cartMenus == null) {
            cartMenus = new ArrayList<>();
        }

        if (platId != null) {
            Plat plat = platService.findPlatById(platId);
            cartPlats.add(plat);
            addedItemName = plat.getNom();
        } else if (menuId != null) {
            Menu menu = menuService.findMenuById(menuId);
            cartMenus.add(menu);
            addedItemName = menu.getNom();
        }

        session.setAttribute("cartPlats", cartPlats);
        session.setAttribute("cartMenus", cartMenus);

        // --- PARTIE IMPORTANTE : On met à jour le compteur ---
        int itemCount = cartPlats.size() + cartMenus.size();
        session.setAttribute("cartItemCount", itemCount);

        // On ajoute un message de succès pour l'afficher sur la page du menu
        redirectAttributes.addFlashAttribute("successMessage", addedItemName + " a été ajouté au panier !");
        return "redirect:/client/menu";
    }

    @GetMapping("/cart")
    @PreAuthorize("hasRole('CLIENT')")
    @SuppressWarnings("unchecked")
    public String showCart(HttpSession session, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (!"guest@resto.com".equals(principal.getName())) {
            User client = userService.findUserByEmail(principal.getName());
            Optional<Commande> commandeActiveOpt = commandeRepository.findActiveCommandeByClientId(client.getId());
            commandeActiveOpt.ifPresent(commande -> model.addAttribute("commandeActive", commande));
        }

        List<Plat> cartPlats = (List<Plat>) session.getAttribute("cartPlats");
        List<Menu> cartMenus = (List<Menu>) session.getAttribute("cartMenus");
        Map<Plat, Long> groupedPlats = (cartPlats != null) ?
                cartPlats.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                : Map.of();

        // Group menus by identity and count occurrences
        Map<Menu, Long> groupedMenus = (cartMenus != null) ?
                cartMenus.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                : Map.of();

        // Calculate total price
        double total = 0.0;
        total += groupedPlats.entrySet().stream().mapToDouble(entry -> entry.getKey().getPrix() * entry.getValue()).sum();
        total += groupedMenus.entrySet().stream().mapToDouble(entry -> entry.getKey().getPrix() * entry.getValue()).sum();

        // Fetch available tables
        List<TableRestaurant> tables = tableRepository.findByStatut(StatutTable.LIBRE);

        model.addAttribute("groupedPlats", groupedPlats);
        model.addAttribute("groupedMenus", groupedMenus);
        model.addAttribute("total", total);
        model.addAttribute("tables", tables);

        return "client/cart";
    }

    @PostMapping("/validate-cart")
    @PreAuthorize("hasRole('CLIENT')")
    @SuppressWarnings("unchecked")
    public String validateCart(@RequestParam(required = false) Long tableId,
                               @RequestParam(required = false) Boolean isEmporter,
                               @RequestParam(required = false) String notes,
                               HttpSession session,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        User client = userService.findUserByEmail(principal.getName());

        Commande commande = null;

        if (!"guest@resto.com".equals(principal.getName())) {
            Optional<Commande> commandeActiveOpt = commandeRepository.findActiveCommandeByClientId(client.getId());
            if (commandeActiveOpt.isPresent()) {
                commande = commandeActiveOpt.get();
            }
        }

        List<Plat> cartPlats = (List<Plat>) session.getAttribute("cartPlats");
        List<Menu> cartMenus = (List<Menu>) session.getAttribute("cartMenus");

        if ((cartPlats == null || cartPlats.isEmpty()) && (cartMenus == null || cartMenus.isEmpty())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Votre panier est vide.");
            return "redirect:/client/cart";
        }

        if (commande == null) {
            TableRestaurant table = null;
            if (Boolean.TRUE.equals(isEmporter)) {
                // Pas de table nécessaire
            } else {
                if (tableId == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Veuillez sélectionner une table ou choisir à emporter.");
                    return "redirect:/client/cart";
                }
                table = tableRepository.findById(tableId).orElse(null);

                if (table == null) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Table invalide.");
                    return "redirect:/client/cart";
                }

                if (table.getStatut() != StatutTable.LIBRE) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Cette table est déjà occupée ou en cours de validation. Veuillez en choisir une autre.");
                    return "redirect:/client/cart";
                }
            }

            commande = new Commande();
            commande.setClient(client);
            commande.setTable(table);
            if (Boolean.TRUE.equals(isEmporter)) {
                commande.setIsEmporter(true);
                commande.setEtat(EtatCommande.EN_ATTENTE);
            } else {
                commande.setEtat(EtatCommande.EN_VALIDATION);
            }
            commande.setDateHeure(LocalDateTime.now());
            commande.setCommentaire(notes);
            commande.setMontantTotal(0.0);
            commande.setLignesCommande(new HashSet<>());
        } else {
            // Commande existe déjà, on la repasse en EN_ATTENTE
            commande.setEtat(EtatCommande.EN_ATTENTE);
            if (notes != null && !notes.trim().isEmpty()) {
                String existingComment = commande.getCommentaire() != null ? commande.getCommentaire() : "";
                commande.setCommentaire(existingComment + (existingComment.isEmpty() ? "" : " | ") + notes);
            }
            if (commande.getMontantTotal() == null) {
                commande.setMontantTotal(0.0);
            }
            if (commande.getLignesCommande() == null) {
                commande.setLignesCommande(new HashSet<>());
            }
        }

        double montantTotal = commande.getMontantTotal();
        Set<LigneCommande> lignes = commande.getLignesCommande();

        if (cartPlats != null) {
            Map<Plat, Long> groupedPlats = cartPlats.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            for (Map.Entry<Plat, Long> entry : groupedPlats.entrySet()) {
                Plat plat = entry.getKey();
                Long qty = entry.getValue();
                LigneCommande ligne = new LigneCommande();
                ligne.setCommande(commande);
                ligne.setPlat(plat);
                ligne.setQuantite(qty.intValue());
                ligne.setTypeLigne(TypeLigneCommande.PLAT);
                lignes.add(ligne);
                montantTotal += plat.getPrix() * qty;
            }
        }

        if (cartMenus != null) {
            Map<Menu, Long> groupedMenus = cartMenus.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            for (Map.Entry<Menu, Long> entry : groupedMenus.entrySet()) {
                Menu menu = entry.getKey();
                Long qty = entry.getValue();
                LigneCommande ligne = new LigneCommande();
                ligne.setCommande(commande);
                ligne.setMenu(menu);
                ligne.setQuantite(qty.intValue());
                ligne.setTypeLigne(TypeLigneCommande.MENU);
                lignes.add(ligne);
                montantTotal += menu.getPrix() * qty;
            }
        }

        commande.setLignesCommande(lignes);
        commande.setMontantTotal(montantTotal);

        commandeService.saveCommande(commande);

        // Clear cart
        session.removeAttribute("cartPlats");
        session.removeAttribute("cartMenus");
        session.removeAttribute("cartItemCount");

        redirectAttributes.addFlashAttribute("successMessage", "Votre commande a été envoyée au serveur pour validation.");
        return "redirect:/client/menu";
    }

    /**
     * Removes an entire item (all quantities) from the cart.
     */
    @GetMapping("/remove-from-cart")
    @PreAuthorize("hasRole('CLIENT')")
    @SuppressWarnings("unchecked")
    public String removeFromCart(@RequestParam(required = false) Long platId,
                                 @RequestParam(required = false) Long menuId,
                                 HttpSession session) {

        if (platId != null) {
            List<Plat> cartPlats = (List<Plat>) session.getAttribute("cartPlats");
            if (cartPlats != null) {
                // Remove all occurrences of this plat
                cartPlats.removeIf(plat -> plat.getIdentifiant().equals(platId));
                session.setAttribute("cartPlats", cartPlats);
            }
        } else if (menuId != null) {
            List<Menu> cartMenus = (List<Menu>) session.getAttribute("cartMenus");
            if (cartMenus != null) {
                // Remove all occurrences of this menu
                cartMenus.removeIf(menu -> menu.getId().equals(menuId));
                session.setAttribute("cartMenus", cartMenus);
            }
        }

        // Recalculate cart item count
        List<Plat> updatedPlats = (List<Plat>) session.getAttribute("cartPlats");
        List<Menu> updatedMenus = (List<Menu>) session.getAttribute("cartMenus");
        int platCount = (updatedPlats != null) ? updatedPlats.size() : 0;
        int menuCount = (updatedMenus != null) ? updatedMenus.size() : 0;
        session.setAttribute("cartItemCount", platCount + menuCount);

        return "redirect:/client/cart";
    }

    @GetMapping("/historique")
    @PreAuthorize("hasRole('CLIENT')")
    public String showHistorique(Model model, Principal principal) {
        if ("guest@resto.com".equals(principal.getName())) {
            model.addAttribute("commandesDuClient", Collections.emptyList());
            return "client/historique";
        }

        User client = userService.findUserByEmail(principal.getName());
        // On cherche toutes ses commandes, en s'assurant de charger les lignes de commande avec
        List<Commande> commandesDuClient = commandeService.findCommandesByClientWithDetails(client);

        model.addAttribute("commandesDuClient", commandesDuClient);
        return "client/historique";
    }

}
