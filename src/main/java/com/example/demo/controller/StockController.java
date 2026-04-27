package com.example.demo.controller;

import com.example.demo.model.Produit;
import com.example.demo.model.StockProduit;
import com.example.demo.model.TypeProduit;
import com.example.demo.repository.StockProduitRepository;
import com.example.demo.service.ProduitService;
import com.example.demo.service.StockService;
import com.example.demo.service.StockAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/stock")
@PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
public class StockController {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockProduitRepository stockProduitRepository;
    @Autowired
    private ProduitService produitService;
    @Autowired
    private com.example.demo.repository.ProduitRepository produitRepository;
    @Autowired
    private StockAiService stockAiService;

    @GetMapping("/list")
    public String listStock(Model model) {
        List<StockProduit> stocks = stockService.findAllStocks();
        // Groupe les stocks par catégorie de produit (TypeProduit)
        Map<TypeProduit, List<StockProduit>> stocksParCategorie = stocks.stream()
                .filter(s -> s.getProduit() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getProduit().getType() != null ? s.getProduit().getType() : TypeProduit.AUTRE,
                        () -> new java.util.TreeMap<>(java.util.Comparator.comparing(TypeProduit::name)),
                        Collectors.toList()
                ));

        model.addAttribute("stocksParCategorie", stocksParCategorie);
        return "stock/list";
    }

    @PostMapping("/update")
    public String updateStock(@RequestParam Long stockId,
                              @RequestParam Integer quantiteSaisie,
                              @RequestParam String typeOperation) {
        stockService.updateStockQuantity(stockId, quantiteSaisie, typeOperation);
        return "redirect:/stock/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String showCreateForm(Model model) {
        StockProduit stock = new StockProduit();
        stock.setProduit(new Produit()); // Initialize to avoid null pointer in view

        model.addAttribute("stock", stock);
        model.addAttribute("types", TypeProduit.values());
        return "stock/form";
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String saveNewStock(@ModelAttribute StockProduit stockProduit) {
        // Enregistrer le nouveau Produit s'il n'existe pas encore
        if (stockProduit.getProduit() != null && stockProduit.getProduit().getId() == null) {
            Produit produitToSave = stockProduit.getProduit();
            Produit savedProduit = produitRepository.save(produitToSave);
            stockProduit.setProduit(savedProduit);
        }

        stockService.saveStock(stockProduit);
        return "redirect:/stock/list";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        StockProduit stock = stockService.findStockById(id);
        List<Produit> produits = produitService.findAllProduits();

        model.addAttribute("stock", stock);
        model.addAttribute("types", TypeProduit.values());
        return "stock/form"; // We use the same view for edit and new
    }

    @PostMapping("/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
    public String updateStock(@ModelAttribute StockProduit stockProduit) {
        // Enregistrer le nouveau Produit s'il n'existe pas encore
        if (stockProduit.getProduit() != null && stockProduit.getProduit().getId() == null) {
            Produit produitToSave = stockProduit.getProduit();
            Produit savedProduit = produitRepository.save(produitToSave);
            stockProduit.setProduit(savedProduit);
        } else if (stockProduit.getProduit() != null) {
            // Find existing produit to merge or update
            Produit existingProduit = produitRepository.findById(stockProduit.getProduit().getId()).orElse(null);
            if (existingProduit != null) {
                existingProduit.setNom(stockProduit.getProduit().getNom());
                existingProduit.setType(stockProduit.getProduit().getType());
                existingProduit.setImage(stockProduit.getProduit().getImage());
                produitRepository.save(existingProduit);
                stockProduit.setProduit(existingProduit);
            } else {
                Produit savedProduit = produitRepository.save(stockProduit.getProduit());
                stockProduit.setProduit(savedProduit);
            }
        }

        stockService.saveStock(stockProduit);
        return "redirect:/stock/list";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteStock(@PathVariable Long id) {
        stockProduitRepository.deleteById(id);
        return "redirect:/stock/list";
    }

    @GetMapping("/low-stock-alert")
    public String showLowStockAlert(Model model) {
        List<StockProduit> lowStocks = stockService.findLowStocks();
        model.addAttribute("lowStocks", lowStocks);
        return "stock/low-stock-alert";
    }

    @GetMapping(value = {"/analyse-ia", "/api/stock/analyse-ia"})
    @ResponseBody
    public String analyseStockIa() {
        return stockAiService.analyseStock();
    }
}
