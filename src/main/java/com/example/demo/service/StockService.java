package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.PlatIngredientRepository;
import com.example.demo.repository.StockProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private PlatIngredientRepository platIngredientRepository;

    @Autowired
    private StockProduitRepository stockProduitRepository;

    public void decrementStockForCommande(Commande commande) {
        // Boucle sur chaque ligne de commande
        for (LigneCommande ligne : commande.getLignesCommande()) {
            if (ligne.getTypeLigne() == TypeLigneCommande.PLAT) {
                Plat plat = ligne.getPlat();
                // Trouver les ingrédients du plat
                List<PlatIngredient> ingredients = platIngredientRepository.findByPlat(plat);
                for (PlatIngredient platIngredient : ingredients) {
                    // Trouver le stock du produit correspondant
                    StockProduit stock = stockProduitRepository.findByProduit(platIngredient.getProduit());
                    if (stock != null) {
                        // Décrémenter le stock
                        double nouveauStock = stock.getStockActuel() - (platIngredient.getQuantite() * ligne.getQuantite());
                        stock.setStockActuel(nouveauStock);
                        stockProduitRepository.save(stock);
                    }
                }
            }
        }
    }
}
