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

    /*@Bean
    public CommandLineRunner dataLoader(
            UserRepository userRepository,
            PlatRepository platRepository,
            ProduitRepository produitRepository,
            StockProduitRepository stockProduitRepository,
            PlatIngredientRepository platIngredientRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // ... (ton code existant pour les utilisateurs et les plats de test)

            // Créer des produits de test
            Produit produitViande = new Produit();
            produitViande.setNom("Boeuf Haché");
            produitViande.setType(TypeProduit.VIANDE);
            produitRepository.save(produitViande);

            Produit produitLegume = new Produit();
            produitLegume.setNom("Tomate");
            produitLegume.setType(TypeProduit.LEGUME);
            produitRepository.save(produitLegume);

            // Créer le stock pour ces produits
            StockProduit stockViande = new StockProduit();
            stockViande.setProduit(produitViande);
            stockViande.setStockActuel(50.0); // 50 kg
            stockViande.setUnite("kg");
            stockViande.setStockMinimum(10.0);
            stockProduitRepository.save(stockViande);

            StockProduit stockLegume = new StockProduit();
            stockLegume.setProduit(produitLegume);
            stockLegume.setStockActuel(30.0); // 30 kg
            stockLegume.setUnite("kg");
            stockLegume.setStockMinimum(5.0);
            stockProduitRepository.save(stockLegume);

            // Créer un plat de test et ses ingrédients
            Plat platBurger = new Plat();
            platBurger.setNom("Cheeseburger");
            platBurger.setPrix(15.0);
            platBurger.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
            platRepository.save(platBurger);

            PlatIngredient ingredientViande = new PlatIngredient();
            ingredientViande.setPlat(platBurger);
            ingredientViande.setProduit(produitViande);
            ingredientViande.setQuantite(0.2); // 200g
            ingredientViande.setUnite("kg");
            platIngredientRepository.save(ingredientViande);

            PlatIngredient ingredientLegume = new PlatIngredient();
            ingredientLegume.setPlat(platBurger);
            ingredientLegume.setProduit(produitLegume);
            ingredientLegume.setQuantite(0.05); // 50g
            ingredientLegume.setUnite("kg");
            platIngredientRepository.save(ingredientLegume);
        };
    }*/
}
