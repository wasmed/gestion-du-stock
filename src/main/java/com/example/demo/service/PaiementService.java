package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.PaiementRepository;
import com.example.demo.repository.PourboireRepository;
import com.example.demo.repository.TableRestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private PourboireRepository pourboireRepository;
    @Autowired
    private TableRestaurantRepository tableRepository;
    @Autowired
    private CommandeService commandeService;

    public Paiement savePaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    public Pourboire savePourboire(Pourboire pourboire) {
        return pourboireRepository.save(pourboire);
    }

    @Transactional
    public void processPayment(Long commandeId, Double montantPourboire, ModePaiement modePaiement) {
        // 1. On récupère la commande
        Commande commande = commandeService.findCommandeById(commandeId);

        // Sécurité : on ne paie pas une commande déjà payée
        if (commande.getEtat() == EtatCommande.PAYEE) {
            return; // Ou lancer une exception
        }

        // --- SIMULATION SERVICE PAIEMENT ---
        if (modePaiement == ModePaiement.CARTE_BANCAIRE) {
            // Ici on appellerait le service externe de paiement (Stripe, Paypal, etc.)
            System.out.println("Processing Card Payment for amount: " + commande.getMontantTotal());
        } else if (modePaiement == ModePaiement.QR_CODE) {
             // Ici on appellerait le service de génération/validation de QR Code
            System.out.println("Processing QR Code Payment for amount: " + commande.getMontantTotal());
        }
        // -----------------------------------

        // 2. Créer le nouvel objet Paiement
        double totalAPayer = commande.getMontantTotal();
        if (montantPourboire != null && montantPourboire > 0) {
            totalAPayer += montantPourboire;
        }

        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMontant(totalAPayer);
        paiement.setServeur(commande.getServeur());
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setStatut(StatutPaiement.PAYE);
        paiement.setModePaiement(modePaiement);
        Paiement savedPaiement = savePaiement(paiement);

        // 3. Gérer le pourboire s'il y en a un
        if (montantPourboire != null && montantPourboire > 0) {
            Pourboire pourboire = new Pourboire();
            pourboire.setMontant(montantPourboire);
            pourboire.setPaiement(savedPaiement);
            savePourboire(pourboire);
        }

        // 4. Mettre à jour l'état de la commande via le service approprié
        commandeService.updateCommandeEtat(commandeId, EtatCommande.PAYEE);

        // 5. Libérer la table
        TableRestaurant table = commande.getTable();
        if (table != null) {
            table.setStatut(StatutTable.LIBRE);
            table.setServeur(null);
            tableRepository.save(table);
        }
    }
}
