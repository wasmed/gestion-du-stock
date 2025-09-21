package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.service.CommandeService;
import com.example.demo.service.MenuService;
import com.example.demo.service.PlatService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    private CommandeRepository commandeRepository;
    @Autowired
    private CommandeService commandeService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private UserService userService;


    @GetMapping("/menu")
    public String showMenu(Model model) {
        List<Plat> plats = platService.findAllPlats();
        List<Menu> menus = menuService.findAllMenus();
        model.addAttribute("plats", plats);
        model.addAttribute("menus", menus);
        return "client/menu"; // Renvoie vers client/menu.html
    }

    @GetMapping("/add-to-cart")
    public String addToCart(@RequestParam(required = false)Long platId,
                            @RequestParam(required = false) Long menuId,HttpSession session) {
        if (platId != null) {
            // Gérer la liste des plats
            List<Plat> cartPlats = (List<Plat>) session.getAttribute("cartPlats");
            if (cartPlats == null) {
                cartPlats = new ArrayList<>();
            }
            Plat plat = platService.findPlatById(platId); // Assure-toi d'avoir cette méthode
            cartPlats.add(plat);
            session.setAttribute("cartPlats", cartPlats);

        } else if (menuId != null) {
            // Gérer la liste des menus
            List<Menu> cartMenus = (List<Menu>) session.getAttribute("cartMenus");
            if (cartMenus == null) {
                cartMenus = new ArrayList<>();
            }
            Menu menu = menuService.findMenuById(menuId);
            cartMenus.add(menu);
            session.setAttribute("cartMenus", cartMenus);
        }

        return "redirect:/client/menu";
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        List<Plat> cartPlats = (List<Plat>) session.getAttribute("cartPlats");
        List<Menu> cartMenus = (List<Menu>) session.getAttribute("cartMenus");

        model.addAttribute("cartPlats", cartPlats != null ? cartPlats : new ArrayList<>());
        model.addAttribute("cartMenus", cartMenus != null ? cartMenus : new ArrayList<>());

        return "client/cart";
    }

    @GetMapping("/historique")
    @PreAuthorize("hasRole('CLIENT')")
    public String showHistorique(Model model, Principal principal) {
        // On récupère le client connecté
        User client = userService.findUserByEmail(principal.getName());
        // On cherche toutes ses commandes
        List<Commande> commandesDuClient = commandeRepository.findByClientIdOrderByDateHeureDesc(client.getId());

        model.addAttribute("commandes", commandesDuClient);
        return "client/historique"; // Chemin vers la nouvelle vue
    }

}
