package com.example.demo.integration;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") // utilise H2
@Transactional
public class CommandeFluxIntegrationTest {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatRepository platRepository;

    @Autowired
    private StockProduitRepository stockProduitRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private CommandeService commandeService;

    @Autowired
    private StockService stockService;

    private User client;
    private Plat plat;
    private Produit tomate;
    private StockProduit stockTomate;
    private LigneCommande ligneCommande;

    @BeforeEach
    public void setup() {
        // Setup Client
        client = new User();
        client.setEmail("testclient@resto.com");
        client.setFullName("Test Client");
        client.setPassword("password");
        client.setRole(Role.CLIENT);
        userRepository.save(client);

        // Setup Produit & Stock
        tomate = new Produit();
        tomate.setNom("Tomate");
        tomate.setType(TypeProduit.LEGUME);
        produitRepository.save(tomate);

        stockTomate = new StockProduit();
        stockTomate.setProduit(tomate);
        stockTomate.setStockActuel(50.0);
        stockTomate.setStockMinimum(10.0);
        stockProduitRepository.save(stockTomate);

        // Setup Plat with Ingredients
        plat = new Plat();
        plat.setNom("Salade de Tomates");
        plat.setPrix(5.0);
        plat.setActif(true);
        plat.setDescription("Une bonne salade");

        Ingredient ingredient = new Ingredient();
        ingredient.setPlat(plat);
        ingredient.setProduit(tomate);
        ingredient.setQuantite(2.0); // 2 tomates par salade

        plat.setIngredients(new ArrayList<>(Arrays.asList(ingredient)));
        platRepository.save(plat);
    }

    @Test
    public void testFluxCommandeClientServeurCuisine() {
        // Etape 1: Le CLIENT ajoute un plat à une nouvelle commande (Simulation du ClientController)
        Commande commande = new Commande();
        commande.setClient(client);
        commande.setEtat(EtatCommande.EN_VALIDATION);
        commande.setDateHeure(LocalDateTime.now());
        commande.setMontantTotal(plat.getPrix());

        Set<LigneCommande> lignes = new HashSet<>();
        ligneCommande = new LigneCommande();
        ligneCommande.setCommande(commande);
        ligneCommande.setPlat(plat);
        ligneCommande.setQuantite(1);
        ligneCommande.setTypeLigne(TypeLigneCommande.PLAT);
        ligneCommande.setEtat(EtatLigneCommande.EN_VALIDATION); // Ligne en attente de validation par le serveur
        lignes.add(ligneCommande);

        commande.setLignesCommande(lignes);
        commande = commandeRepository.save(commande);

        // Vérification Etape 1 : Le stock doit être intact (50.0)
        StockProduit stockAvantValidation = stockProduitRepository.findById(stockTomate.getId()).orElseThrow();
        assertThat(stockAvantValidation.getStockActuel()).isEqualTo(50.0);

        // Etape 2: Le SERVEUR valide l'ajout (Simulation de OrderManagementController.validateOrderLines)
        LigneCommande savedLigne = commande.getLignesCommande().iterator().next();
        savedLigne.setEtat(EtatLigneCommande.EN_ATTENTE);
        ligneCommandeRepository.save(savedLigne);
        stockService.processStockDecrementForLigne(savedLigne); // Décrémente le stock

        // Vérification Etape 2 : Le stock doit être décrémenté (50.0 - 2.0 = 48.0)
        StockProduit stockApresValidation = stockProduitRepository.findById(stockTomate.getId()).orElseThrow();
        assertThat(stockApresValidation.getStockActuel()).isEqualTo(48.0);
        assertThat(savedLigne.getEtat()).isEqualTo(EtatLigneCommande.EN_ATTENTE);

        // Etape 3: Le CHEF termine la préparation (Simulation de OrderManagementController.finishPreparationLigne)
        commandeService.updateLigneCommandeEtat(savedLigne.getIdentifiant(), EtatLigneCommande.PREPARATION_TERMINEE);

        LigneCommande ligneApresPrepa = ligneCommandeRepository.findById(savedLigne.getIdentifiant()).orElseThrow();
        assertThat(ligneApresPrepa.getEtat()).isEqualTo(EtatLigneCommande.PREPARATION_TERMINEE);

        // Etape 4: Le SERVEUR sert le plat (Simulation de OrderManagementController.serveOrder)
        ligneApresPrepa.setEtat(EtatLigneCommande.SERVIE);
        ligneCommandeRepository.save(ligneApresPrepa);

        LigneCommande ligneServie = ligneCommandeRepository.findById(savedLigne.getIdentifiant()).orElseThrow();
        assertThat(ligneServie.getEtat()).isEqualTo(EtatLigneCommande.SERVIE);

        // Vérification globale: la commande doit être considérée comme prête si toutes les lignes sont servies
        Commande commandeRefresh = commandeService.findCommandeById(commande.getId());
        assertThat(commandeRefresh.isToutesLignesServies()).isTrue();
    }
}
