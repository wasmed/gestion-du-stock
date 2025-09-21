package com.example.demo.controller;

import com.example.demo.model.Produit;
import com.example.demo.model.StockProduit;
import com.example.demo.service.ProduitService;
import com.example.demo.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/stock")
@PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
public class StockController {

    @Autowired
    private StockService stockService;

    @Autowired
    private ProduitService produitService;

    @GetMapping
    public String listStock(Model model) {
        List<StockProduit> stocks = stockService.findAllStocks();
        model.addAttribute("stocks", stocks);
        return "stock/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        StockProduit stock = stockService.findStockById(id);
        List<Produit> produits = produitService.findAllProduits();

        model.addAttribute("stock", stock);
        model.addAttribute("produits", produits);
        return "stock/edit-form";
    }

    @PostMapping("/edit")
    public String updateStock(@ModelAttribute StockProduit stockProduit) {
        stockService.saveStock(stockProduit);
        return "redirect:/stock";
    }

    @GetMapping("/low-stock-alert")
    public String showLowStockAlert(Model model) {
        List<StockProduit> lowStocks = stockService.findLowStocks();
        model.addAttribute("lowStocks", lowStocks);
        return "stock/low-stock-alert";
    }
}
