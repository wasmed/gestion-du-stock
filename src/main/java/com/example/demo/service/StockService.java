package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.PlatIngredientRepository;
import com.example.demo.repository.StockProduitRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class StockService {

    @Autowired
    private PlatIngredientRepository platIngredientRepository;

    @Autowired
    private StockProduitRepository stockProduitRepository;

    @Transactional
    public void decrementStockForCommande(Commande commande) {
        Set<LigneCommande> lignesCommande = commande.getLignesCommande();
        if (lignesCommande != null && !lignesCommande.isEmpty()) {
            for (LigneCommande ligne : lignesCommande) {
                if (ligne.getTypeLigne() == TypeLigneCommande.PLAT) {
                    decrementStockForPlat(ligne.getPlat(), ligne.getQuantite());
                } else if (ligne.getTypeLigne() == TypeLigneCommande.MENU) {
                    decrementStockForMenu(ligne.getMenu(), ligne.getQuantite());
                }
            }
        }
    }

    private void decrementStockForPlat(Plat plat, int quantiteLigne) {
        List<PlatIngredient> ingredients = platIngredientRepository.findByPlat(plat);
        for (PlatIngredient platIngredient : ingredients) {
            StockProduit stock = stockProduitRepository.findByProduit(platIngredient.getProduit());
            if (stock != null) {
                double nouveauStock = stock.getStockActuel() - (platIngredient.getQuantite() * quantiteLigne);
                stock.setStockActuel(nouveauStock);
                stockProduitRepository.save(stock);
            } else {
                // Log an alert if a product is not in stock
                System.err.println("ALERTE: Stock pour le produit " + platIngredient.getProduit().getNom() + " non trouvé.");
            }
        }
    }

    private void decrementStockForMenu(Menu menu, int quantiteMenu) {
        for (Plat plat : menu.getPlats()) {
            decrementStockForPlat(plat, quantiteMenu);
        }
    }

    public List<StockProduit> findAllStocks() {
        return stockProduitRepository.findAll();
    }
}
