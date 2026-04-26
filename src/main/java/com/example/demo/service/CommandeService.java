package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeService {

    @Autowired
    private ConsommationStockRepository consommationStockRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;
    @Autowired
    private TableRestaurantRepository tableRepository;
    @Autowired
    private PlatRepository platRepository;

    public ConsommationStock createConsommation(LigneCommande ligneCommande, Produit produit, Double quantiteUtilisee) {
        ConsommationStock consommation = new ConsommationStock();
        consommation.setLigneCommande(ligneCommande);
        consommation.setProduit(produit);
        consommation.setQuantiteUtilisee(quantiteUtilisee);
        return consommationStockRepository.save(consommation);
    }

    public Commande createNewCommande(User client, User serveur,Long tableId) {
        TableRestaurant table = tableRepository.findById(tableId).orElse(null);
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setServeur(serveur);
        commande.setTable(table);
        commande.setEtat(EtatCommande.EN_COURS);
        return commandeRepository.save(commande);
    }
    public Commande createNewCommande(User client, User serveur) {
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setServeur(serveur);
        commande.setEtat(EtatCommande.EN_COURS);
        return commandeRepository.save(commande);
    }

    @Autowired
    private StockService stockService;

    public Commande addPlatToCommande(Commande commande, Long platId, int quantite) {
        Plat plat = platRepository.findById(platId).orElseThrow(() -> new IllegalArgumentException("Plat non trouvé"));
        LigneCommande ligne = new LigneCommande();
        ligne.setCommande(commande);
        ligne.setQuantite(quantite);
        ligne.setPlat(plat); // Link the order line to the plat
        ligne.setTypeLigne(TypeLigneCommande.PLAT); // Set the type to PLAT
        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
        ligne = ligneCommandeRepository.save(ligne);

        // Decrement stock immediately
        stockService.processStockDecrementForLigne(ligne);

        return commande;
    }

    public Commande addMenuToCommande(Commande commande, Long menuId,int quantite) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new IllegalArgumentException("Menu non trouvé"));
        LigneCommande ligne = new LigneCommande();
        ligne.setCommande(commande);
        ligne.setQuantite(quantite);
        ligne.setMenu(menu); // Link the order line to the menu
        ligne.setTypeLigne(TypeLigneCommande.MENU); // Set the type to MENU
        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
        ligne = ligneCommandeRepository.save(ligne);

        // Decrement stock immediately
        stockService.processStockDecrementForLigne(ligne);

        return commande;
    }

    public List<Commande> getCommandesEnCours() {
        return commandeRepository.findByEtat(EtatCommande.EN_COURS);
    }

    public List<Commande> getCommandesAValider() {
        // Charge les commandes à valider avec leurs détails
        return commandeRepository.findByEtatInWithDetails(List.of(EtatCommande.EN_VALIDATION));
    }

    public Commande updateCommandeEtat(Long commandeId, EtatCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(commandeId).orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
        commande.setEtat(nouveauStatut);
        return commandeRepository.save(commande);
    }

    public LigneCommande saveLigneCommande(LigneCommande ligne) {
        return ligneCommandeRepository.save(ligne);
    }

    public Commande saveCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Transactional
    public Commande findCommandeById(Long id) {
        return commandeRepository.findByIdentifiantWithLignesCommande(id)
                .orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
    }

    public List<Commande> getCommandesEnCoursEtServies() {
        List<EtatCommande> etats = List.of(
                EtatCommande.EN_COURS
        );
        // On appelle la nouvelle méthode du repository
        return commandeRepository.findByEtatInWithDetails(etats);
    }

    public List<Commande> getCommandesEnAttenteEtEnPreparation() {
        List<EtatCommande> etats = List.of(
                EtatCommande.EN_COURS
        );
        // On utilise la méthode optimisée pour charger les détails
        return commandeRepository.findByEtatInWithDetails(etats);
    }

    public void updateLigneCommandeEtat(Long id, EtatLigneCommande etat) {
        LigneCommande ligne = ligneCommandeRepository.findById(id).orElse(null);
        if (ligne != null) {
            ligne.setEtat(etat);
            ligneCommandeRepository.save(ligne);
        }
    }

    public List<Commande> findCommandesByClientWithDetails(User client) {
        return commandeRepository.findByClientWithLignesCommande(client);
    }



    @Transactional
    public void deleteOrder(Long id) {
        Commande commande = commandeRepository.findById(id).orElse(null);
        if (commande != null) {
            TableRestaurant table = commande.getTable();
            commandeRepository.delete(commande);

            if (table != null) {
                table.setStatut(StatutTable.LIBRE);
                table.setServeur(null);
                tableRepository.save(table);
            }
        }
    }
}
