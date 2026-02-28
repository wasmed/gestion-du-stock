package com.example.demo.controller;

import com.example.demo.model.CategoriePlat;
import com.example.demo.model.Plat;
import com.example.demo.service.PlatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/plats")
public class PlatController {

    @Autowired
    private PlatService platService;

    // Affiche la liste des plats
    @GetMapping
    public String listPlats(Model model) {
        model.addAttribute("plats", platService.findAllPlats());
        return "plat/list"; // Renvoie vers plat/list.html
    }

    @Autowired
    private com.example.demo.repository.FormatProduitRepository formatProduitRepository;
    @Autowired
    private com.example.demo.service.ProduitService produitService;

    // Affiche le formulaire d'ajout/modification
    @GetMapping("/form")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String showPlatForm(@RequestParam(required = false) Long id, Model model) {
        Plat plat = (id != null) ? platService.findPlatById(id) : new Plat();
        model.addAttribute("plat", plat);
        model.addAttribute("categories", CategoriePlat.values());
        model.addAttribute("produits", produitService.findAllProduits());
        model.addAttribute("formats", formatProduitRepository.findAll());
        return "plat/form"; // Renvoie vers plat/form.html
    }

    // Gère la soumission du formulaire
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String savePlat(@ModelAttribute Plat plat) {
        // Link ingredients back to plat to cascade save correctly
        if (plat.getIngredients() != null) {
            for (com.example.demo.model.Ingredient ing : plat.getIngredients()) {
                ing.setPlat(plat);
            }
        }
        platService.savePlat(plat);
        return "redirect:/plats";
    }

    // Gère la suppression d'un plat
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePlat(@PathVariable Long id) {
        platService.deletePlatById(id);
        return "redirect:/plats";
    }
}
