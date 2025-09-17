package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.PaiementRepository;
import com.example.demo.repository.PourboireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private PourboireRepository pourboireRepository;

    @Autowired
    private CommandeService commandeService;

    public Paiement savePaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public Pourboire savePourboire(Pourboire pourboire) {
        return pourboireRepository.save(pourboire);
    }

    public void processPayment(Long commandeId, Double montantPourboire,ModePaiement modePaiement) {
        Commande commande = commandeService.findCommandeById(commandeId);

        // Créer un nouvel objet Paiement
        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMontant(commande.getMontantTotal()); // Le montant est le total de la commande
        paiement.setServeur(commande.getServeur()); // Le serveur est celui qui a validé la commande
        paiement.setDatePaiement(LocalDateTime.now());
        Paiement savedPaiement = savePaiement(paiement);
        paiement.setStatut(StatutPaiement.PAYE);
        paiement.setModePaiement(modePaiement);

        // Gérer le pourboire s'il y en a un
        if (montantPourboire != null && montantPourboire > 0) {
            Pourboire pourboire = new Pourboire();
            pourboire.setMontant(montantPourboire);
            pourboire.setPaiement(savedPaiement);
            savePourboire(pourboire);
        }

        // Mise à jour de l'état de la commande à PAYEE
        commande.setEtat(EtatCommande.PAYEE);
        commandeService.saveCommande(commande);
    }
}
