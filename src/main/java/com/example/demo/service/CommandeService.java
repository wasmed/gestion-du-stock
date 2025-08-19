package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.LigneCommandeRepository;
import com.example.demo.repository.PlatRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private PlatRepository platRepository;

    public Commande createNewCommande(User client, User serveur) {
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setServeur(serveur);
        commande.setEtat(EtatCommande.EN_ATTENTE);
        return commandeRepository.save(commande);
    }

    public Commande addPlatToCommande(Commande commande, Long platId, int quantite) {
        Plat plat = platRepository.findById(platId).orElseThrow(() -> new IllegalArgumentException("Plat non trouvé"));
        LigneCommande ligne = new LigneCommande();
        ligne.setCommande(commande);
        ligne.setQuantite(quantite);
        // Ici, il faudrait aussi lier le plat à la ligne de commande, en fonction de ton implémentation
        ligneCommandeRepository.save(ligne);
        return commande;
    }

    public List<Commande> getCommandesEnAttente() {
        return commandeRepository.findByEtat(EtatCommande.EN_ATTENTE);
    }

    public Commande updateCommandeEtat(Long commandeId, EtatCommande nouveauStatut) {
        Commande commande = commandeRepository.findById(commandeId).orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
        commande.setEtat(nouveauStatut);
        return commandeRepository.save(commande);
    }

    public List<Commande> getCommandesEnCours() {
        // Récupérer les commandes en attente et en préparation
        return commandeRepository.findByEtatIn(List.of(EtatCommande.EN_ATTENTE, EtatCommande.EN_PREPARATION));
    }

    public LigneCommande saveLigneCommande(LigneCommande ligne) {
        return ligneCommandeRepository.save(ligne);
    }

    public Commande saveCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Transactional
    public Commande findCommandeById(Long id) {
        return commandeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Commande non trouvée"));
    }
}
