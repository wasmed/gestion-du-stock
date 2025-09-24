package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.CommandeRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String showCart(HttpSession session, Model model) {
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

        model.addAttribute("groupedPlats", groupedPlats);
        model.addAttribute("groupedMenus", groupedMenus);
        model.addAttribute("total", total);

        return "client/cart";
    }

    /**
     * Removes an entire item (all quantities) from the cart.
     */
    @GetMapping("/remove-from-cart")
    @PreAuthorize("hasRole('CLIENT')")
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
