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
            CommandeRepository commandeRepository,
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

            // Créer les plats et le stock si la table est vide
            if (platRepository.count() == 0) {
                Plat plat1 = new Plat();
                plat1.setNom("Pizza Margherita");
                plat1.setPrix(10.50);
                plat1.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
                platRepository.save(plat1);

                Produit produit1 = new Produit();
                produit1.setNom("Farine");
                produitRepository.save(produit1);

                StockProduit stock1 = new StockProduit();
                stock1.setProduit(produit1);
                stock1.setStockActuel(50.0);
                stock1.setStockMinimum(10.0);
                stock1.setUnite("kg");
                stockProduitRepository.save(stock1);

                // NOUVELLE LOGIQUE : Création d'un INGREDIENT qui fait le lien
                Ingredient ingredient1 = new Ingredient();
                ingredient1.setPlat(plat1);
                ingredient1.setProduit(produit1);
                ingredient1.setQuantite(0.5);
                ingredient1.setUnite("kg");
                ingredientRepository.save(ingredient1);
            }

        };
    }
}

