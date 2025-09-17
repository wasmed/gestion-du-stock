package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String motDePasseEnClair = "motdepasse123";
        String motDePasseEnClair1 = "motdepasse1234";
        String motDePasseEnClair2 = "motdepasse12345";
        String motDePasseEnClair3 = "motdepasse123456";
        String motDePasseHache = encoder.encode(motDePasseEnClair);
        String motDePasseHache1 = encoder.encode(motDePasseEnClair1);
        String motDePasseHache2 = encoder.encode(motDePasseEnClair2);
        String motDePasseHache3 = encoder.encode(motDePasseEnClair3);

        System.out.println("Mot de passe haché CLIENT: " + motDePasseHache);
        System.out.println("Mot de passe haché ADMIN: " + motDePasseHache1);
        System.out.println("Mot de passe haché CHEF CUISINIER: " + motDePasseHache2);
        System.out.println("Mot de passe haché SERVEUR: " + motDePasseHache3);
    }

    @Bean
    public CommandLineRunner dataLoader(
            UserRepository userRepository,
            PlatRepository platRepository,
            ProduitRepository produitRepository,
            StockProduitRepository stockProduitRepository,
            PlatIngredientRepository platIngredientRepository,
            CommandeRepository commandeRepository,
            LigneCommandeRepository ligneCommandeRepository,
            TableRestaurantRepository tableRepository, // Ajoute cette injection
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Charger les tables de test
            if (tableRepository.count() == 0) {
                // Créer et sauvegarder des tables
                TableRestaurant table1 = new TableRestaurant();
                table1.setNumeroTable(1);
                tableRepository.save(table1);

                TableRestaurant table2 = new TableRestaurant();
                table2.setNumeroTable(2);
                tableRepository.save(table2);

                // ... et ainsi de suite pour les autres tables
            }

            // ... le reste de ton code pour les utilisateurs, plats, etc.
        };
    }
   /* @Bean
    public CommandLineRunner dataLoader(
            UserRepository userRepository,
            PlatRepository platRepository,
            ProduitRepository produitRepository,
            StockProduitRepository stockProduitRepository,
            PlatIngredientRepository platIngredientRepository,
            CommandeRepository commandeRepository,
            LigneCommandeRepository ligneCommandeRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Créer les utilisateurs de test
            User client = new User();
            client.setEmail("client@restaurant.com");
            client.setNom("Client Test");
            client.setMotDePasse(passwordEncoder.encode("motdepasse123"));
            client.setRole(Role.CLIENT);
            userRepository.save(client);

            User admin = new User();
            admin.setEmail("admin@restaurant.com");
            admin.setNom("Admin Test");
            admin.setMotDePasse(passwordEncoder.encode("motdepasse1234"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            User chef = new User();
            chef.setEmail("chef@restaurant.com");
            chef.setNom("Chef Cuisinier Test");
            chef.setMotDePasse(passwordEncoder.encode("motdepasse12345"));
            chef.setRole(Role.CHEF_CUISINIER);
            userRepository.save(chef);

            User serveur = new User();
            serveur.setEmail("serveur@restaurant.com");
            serveur.setNom("Serveur Test");
            serveur.setMotDePasse(passwordEncoder.encode("motdepasse123456"));
            serveur.setRole(Role.SERVEUR);
            userRepository.save(serveur);

            // 2. Créer les produits de test et le stock initial
            Produit boeufHache = new Produit();
            boeufHache.setNom("Boeuf Haché");
            boeufHache.setType(TypeProduit.VIANDE);
            produitRepository.save(boeufHache);

            StockProduit stockBoeuf = new StockProduit();
            stockBoeuf.setProduit(boeufHache);
            stockBoeuf.setStockActuel(50.0);
            stockBoeuf.setUnite("kg");
            stockBoeuf.setStockMinimum(10.0);
            stockProduitRepository.save(stockBoeuf);

            // 3. Créer un plat de test qui utilise le stock
            Plat cheeseburger = new Plat();
            cheeseburger.setNom("Cheeseburger");
            cheeseburger.setPrix(15.0);
            cheeseburger.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
            platRepository.save(cheeseburger);

            // 4. Lier le plat au produit (ingrédient)
            PlatIngredient ingredientBoeuf = new PlatIngredient();
            ingredientBoeuf.setPlat(cheeseburger);
            ingredientBoeuf.setProduit(boeufHache);
            ingredientBoeuf.setQuantite(0.2); // 200g
            ingredientBoeuf.setUnite("kg");
            platIngredientRepository.save(ingredientBoeuf);

            // 5. Créer une commande de test pour le flux de décrémentation
            Commande commandeTest = new Commande();
            commandeTest.setClient(client);
            commandeTest.setServeur(serveur);
            commandeTest.setEtat(EtatCommande.EN_ATTENTE);
            commandeTest.setDateHeure(LocalDateTime.now());
            commandeTest.setMontantTotal(15.0);
            commandeRepository.save(commandeTest);

            LigneCommande ligneCommandeTest = new LigneCommande();
            ligneCommandeTest.setCommande(commandeTest);
            ligneCommandeTest.setPlat(cheeseburger);
            ligneCommandeTest.setQuantite(1);
            ligneCommandeTest.setTypeLigne(TypeLigneCommande.PLAT);
            ligneCommandeRepository.save(ligneCommandeTest);
        };
    }*/
}
