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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

/*
    @Bean
    public CommandLineRunner dataLoader(
            UserRepository userRepository,
            MenuRepository menuRepository,
            PlatRepository platRepository,
            ProduitRepository produitRepository,
            StockProduitRepository stockProduitRepository,
            FormatProduitRepository formatProduitRepository,
            LigneCommandeRepository ligneCommandeRepository,
            IngredientRepository ingredientRepository,
            TableRestaurantRepository tableRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Créer les utilisateurs si la table est vide


            // Créer les tables si elles n'existent pas
            if (tableRepository.count() == 0) {
                TableRestaurant table1 = new TableRestaurant();
                table1.setNumeroTable(1);
                table1.setNombrePersonne(2); // Nombre de personnes sur cette table
                tableRepository.save(table1);

                TableRestaurant table2 = new TableRestaurant();
                table2.setNumeroTable(2);
                table2.setNombrePersonne(4);
                tableRepository.save(table2);

                TableRestaurant table3 = new TableRestaurant();
                table3.setNumeroTable(3);
                table3.setNombrePersonne(6);
                tableRepository.save(table3);
            }
            // =================================================================
            // 0. CREATE PRODUCT FORMATS (The different packaging/units)
            // =================================================================
            FormatProduit formatKg = new FormatProduit();
            formatKg.setNom("Kilogramme");
            formatKg.setQuantite(1.0);

            FormatProduit formatUnite = new FormatProduit();
            formatUnite.setNom("Unité");
            formatUnite.setQuantite(1.0);

            FormatProduit formatBouteille50cl = new FormatProduit();
            formatBouteille50cl.setNom("Bouteille 50cl");
            formatBouteille50cl.setQuantite(0.5);

            FormatProduit formatCanette33cl = new FormatProduit();
            formatCanette33cl.setNom("Canette 33cl");
            formatCanette33cl.setQuantite(0.33);

            formatProduitRepository.saveAll(Arrays.asList(formatKg, formatUnite, formatBouteille50cl, formatCanette33cl));

            // =================================================================
            // 1. CREATE PRODUCTS (The description of the item)
            // =================================================================
            Produit tomate = new Produit();
            tomate.setNom("Tomate");
            tomate.setType(TypeProduit.LEGUME);

            Produit salade = new Produit();
            salade.setNom("Salade Laitue");
            salade.setType(TypeProduit.LEGUME);

            Produit patePizza = new Produit();
            patePizza.setNom("Pâte à Pizza");
            patePizza.setType(TypeProduit.BOULANGERIE);

            Produit mozzarella = new Produit();
            mozzarella.setNom("Mozzarella");
            mozzarella.setType(TypeProduit.CREMERIE);

            Produit eauPlateProduit = new Produit();
            eauPlateProduit.setNom("Eau Plate 50cl");
            eauPlateProduit.setType(TypeProduit.BOISSON);

            Produit chocolatNoir = new Produit();
            chocolatNoir.setNom("Chocolat Noir 70%");
            chocolatNoir.setType(TypeProduit.DESSERT);

            // Save all products first to get their IDs
            List<Produit> produits = Arrays.asList(tomate, salade, patePizza, mozzarella, eauPlateProduit, chocolatNoir);
            produitRepository.saveAll(produits);

            // =================================================================
            // 2. CREATE THE STOCK ASSOCIATED WITH EACH PRODUCT
            // =================================================================
            StockProduit stockTomate = new StockProduit();
            stockTomate.setProduit(tomate);
            stockTomate.setStockActuel(50.0);
            stockTomate.setStockMinimum(10.0);
            stockTomate.setUnite("kg");

            StockProduit stockSalade = new StockProduit();
            stockSalade.setProduit(salade);
            stockSalade.setStockActuel(20.0);
            stockSalade.setStockMinimum(5.0);
            stockSalade.setUnite("kg");

            StockProduit stockPatePizza = new StockProduit();
            stockPatePizza.setProduit(patePizza);
            stockPatePizza.setStockActuel(20.0);
            stockPatePizza.setStockMinimum(4.0);
            stockPatePizza.setUnite("kg");

            StockProduit stockMozzarella = new StockProduit();
            stockMozzarella.setProduit(mozzarella);
            stockMozzarella.setStockActuel(15.0);
            stockMozzarella.setStockMinimum(3.0);
            stockMozzarella.setUnite("kg");

            StockProduit stockEau = new StockProduit();
            stockEau.setProduit(eauPlateProduit);
            stockEau.setStockActuel(100.0);
            stockEau.setStockMinimum(24.0);
            stockEau.setUnite("bouteille");

            StockProduit stockChocolat = new StockProduit();
            stockChocolat.setProduit(chocolatNoir);
            stockChocolat.setStockActuel(10.0);
            stockChocolat.setStockMinimum(2.0);
            stockChocolat.setUnite("kg");

            List<StockProduit> stocks = Arrays.asList(stockTomate, stockSalade, stockPatePizza, stockMozzarella, stockEau, stockChocolat);
            stockProduitRepository.saveAll(stocks);

            // =================================================================
            // 3. CREATE DISHES (which use the products as ingredients)
            // =================================================================
            Plat saladeVerte = new Plat();
            saladeVerte.setNom("Salade Verte Simple");
            saladeVerte.setDescription("Une salade fraîche de saison.");
            saladeVerte.setPrix(7.50);
            saladeVerte.setCategorie(CategoriePlat.ENTREE);
            platRepository.save(saladeVerte);

            Ingredient ingSalade = new Ingredient();
            ingSalade.setPlat(saladeVerte);
            ingSalade.setProduit(salade);
            ingSalade.setQuantite(0.150);
            ingSalade.setUnite("kg");
            ingredientRepository.save(ingSalade);

            Ingredient ingTomateSalade = new Ingredient();
            ingTomateSalade.setPlat(saladeVerte);
            ingTomateSalade.setProduit(tomate);
            ingTomateSalade.setQuantite(0.080);
            ingTomateSalade.setUnite("kg");
            ingredientRepository.save(ingTomateSalade);

            Plat pizzaMargherita = new Plat();
            pizzaMargherita.setNom("Pizza Margherita");
            pizzaMargherita.setDescription("Sauce tomate, mozzarella fondante et basilic frais.");
            pizzaMargherita.setPrix(12.00);
            pizzaMargherita.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
            platRepository.save(pizzaMargherita);

            Ingredient ingPate = new Ingredient();
            ingPate.setPlat(pizzaMargherita);
            ingPate.setProduit(patePizza);
            ingPate.setQuantite(0.250);
            ingPate.setUnite("kg");
            ingredientRepository.save(ingPate);

            Ingredient ingMozzaPizza = new Ingredient();
            ingMozzaPizza.setPlat(pizzaMargherita);
            ingMozzaPizza.setProduit(mozzarella);
            ingMozzaPizza.setQuantite(0.120);
            ingMozzaPizza.setUnite("kg");
            ingredientRepository.save(ingMozzaPizza);

            Ingredient ingTomatePizza = new Ingredient();
            ingTomatePizza.setPlat(pizzaMargherita);
            ingTomatePizza.setProduit(tomate);
            ingTomatePizza.setQuantite(0.100);
            ingTomatePizza.setUnite("kg");
            ingredientRepository.save(ingTomatePizza);

            Plat boissonEau = new Plat();
            boissonEau.setNom("Eau Plate");
            boissonEau.setDescription("Bouteille de 50cl.");
            boissonEau.setPrix(3.00);
            boissonEau.setCategorie(CategoriePlat.BOISSON);
            platRepository.save(boissonEau);

            Ingredient ingEau = new Ingredient();
            ingEau.setPlat(boissonEau);
            ingEau.setProduit(eauPlateProduit);
            ingEau.setQuantite(1.0);
            ingEau.setUnite("bouteille");
            ingredientRepository.save(ingEau);

            // =================================================================
            // 4. CREATE MENUS (which group dishes)
            // =================================================================
            Menu menuDuJour = new Menu();
            menuDuJour.setNom("Menu du Jour");
            menuDuJour.setDescription("L'essentiel de notre cuisine.");
            menuDuJour.setPrix(24.00);
            menuDuJour.setPlats(new HashSet<>(Arrays.asList(saladeVerte, pizzaMargherita)));
            menuRepository.save(menuDuJour);


        };
    }*/
}

