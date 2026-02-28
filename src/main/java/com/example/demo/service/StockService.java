package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.ConsommationStockRepository;
import com.example.demo.repository.IngredientRepository;
import com.example.demo.repository.StockProduitRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StockProduitRepository stockProduitRepository;
    @Autowired
    private IngredientRepository ingredientRepository; // Inject the new repository

    @Autowired
    private ConsommationStockRepository consommationStockRepository;

    @Transactional
    public void processStockDecrementForCommande(Commande commande) {
        Set<LigneCommande> lignesCommande = commande.getLignesCommande();
        if (lignesCommande != null && !lignesCommande.isEmpty()) {
            for (LigneCommande ligne : lignesCommande) {
                if (ligne.getTypeLigne() == TypeLigneCommande.PLAT) {
                    processStockForPlat(ligne.getPlat(), ligne);
                } else if (ligne.getTypeLigne() == TypeLigneCommande.MENU) {
                    processStockForMenu(ligne.getMenu(), ligne);
                }
            }
        }
    }

    private void processStockForPlat(Plat plat, LigneCommande ligneCommande) {
        List<Ingredient> ingredients = ingredientRepository.findByPlat(plat);
        for (Ingredient ingredient : ingredients) {
            StockProduit stock = stockProduitRepository.findByProduit(ingredient.getProduit());
            if (stock != null) {
                double quantiteAUtiliser = ingredient.getQuantite() * ligneCommande.getQuantite();

                // Création d'un enregistrement ConsommationStock pour l'historique
                ConsommationStock consommation = new ConsommationStock();
                consommation.setLigneCommande(ligneCommande);
                consommation.setProduit(ingredient.getProduit());

                consommation.setQuantiteUtilisee(quantiteAUtiliser);
                consommationStockRepository.save(consommation);

                // Décrémentation effective du stock
                stock.setStockActuel(stock.getStockActuel() - quantiteAUtiliser);
                stockProduitRepository.save(stock);
            } else {
                System.err.println("ALERTE: Stock pour le produit " + ingredient.getProduit().getNom() + " non trouvé.");
            }
        }
    }

    private void processStockForMenu(Menu menu, LigneCommande ligneCommande) {
        for (Plat plat : menu.getPlats()) {
            processStockForPlat(plat, ligneCommande);
        }
    }

    public List<StockProduit> findAllStocks() {
        return stockProduitRepository.findAll();
    }

    public StockProduit findStockById(Long id) {
        return stockProduitRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateStockQuantity(Long stockId, Integer quantiteSaisie, String typeOperation) {
        StockProduit stockProduit = stockProduitRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Stock non trouvé pour l'ID: " + stockId));

        double quantiteFormat = 1.0;
        if (stockProduit.getFormatProduit() != null && stockProduit.getFormatProduit().getQuantite() != null) {
            quantiteFormat = stockProduit.getFormatProduit().getQuantite();
        }

        double quantiteARajuster = quantiteSaisie * quantiteFormat;

        if ("AJOUT".equals(typeOperation)) {
            stockProduit.setStockActuel(stockProduit.getStockActuel() + quantiteARajuster);
        } else if ("RETRAIT".equals(typeOperation)) {
            stockProduit.setStockActuel(stockProduit.getStockActuel() - quantiteARajuster);
        } else {
            throw new IllegalArgumentException("Opération non supportée: " + typeOperation);
        }

        stockProduitRepository.save(stockProduit);
    }

    public StockProduit saveStock(StockProduit stockProduit) {
        return stockProduitRepository.save(stockProduit);
    }

    public List<StockProduit> findLowStocks() {
        return stockProduitRepository.findAll().stream()
                .filter(stock -> stock.getStockActuel() < stock.getStockMinimum())
                .collect(Collectors.toList());
    }
}
