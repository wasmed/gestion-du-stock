package com.example.demo.controller;

import com.example.demo.model.*;
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

}
