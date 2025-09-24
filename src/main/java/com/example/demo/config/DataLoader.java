package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlatRepository platRepository;
    private final MenuRepository menuRepository;
    private final ProduitRepository produitRepository;
    private final IngredientRepository ingredientRepository;
    private final FormatProduitRepository formatProduitRepository;
    private final StockProduitRepository stockProduitRepository;
    private final UserRepository userRepository; // <-- DEPENDENCY ADDED
    private final TableRestaurantRepository tableRepository; // <-- DEPENDENCY ADDED
    private final PasswordEncoder passwordEncoder;

    public DataLoader(PlatRepository platRepository, MenuRepository menuRepository,
                      ProduitRepository produitRepository, IngredientRepository ingredientRepository,
                      FormatProduitRepository formatProduitRepository,StockProduitRepository stockProduitRepository,UserRepository userRepository, TableRestaurantRepository tableRepository, PasswordEncoder passwordEncoder) { // <-- DÉPENDANCE AJOUTÉE
        this.platRepository = platRepository;
        this.menuRepository = menuRepository;
        this.produitRepository = produitRepository;
        this.ingredientRepository = ingredientRepository;
        this.formatProduitRepository = formatProduitRepository;
        this.stockProduitRepository = stockProduitRepository;
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (produitRepository.count() == 0) {
            System.out.println("Base de données vide, chargement des données de test...");
            loadData();
        } else {
            System.out.println("La base de données contient déjà des données.");
        }
    }

    private void loadData() {
        // --- USERS ---
        User admin = new User();
        admin.setFullName("Admin General");
        admin.setEmail("admin@resto.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);

        User chef = new User();
        chef.setFullName("Chef Cuisinier");
        chef.setEmail("chef@resto.com");
        chef.setPassword(passwordEncoder.encode("chef123"));
        chef.setRole(Role.CHEF_CUISINIER);

        User serveur = new User();
        serveur.setFullName("Serveur Un");
        serveur.setEmail("serveur@resto.com");
        serveur.setPassword(passwordEncoder.encode("serveur123"));
        serveur.setRole(Role.SERVEUR);

        User client = new User();
        client.setFullName("Jean Dupont");
        client.setEmail("client@resto.com");
        client.setPassword(passwordEncoder.encode("client123"));
        client.setRole(Role.CLIENT);

        User guest = new User();
        guest.setFullName("Client Invité");
        guest.setEmail("guest@resto.com"); // As defined in business rules
        guest.setPassword(passwordEncoder.encode("guest123"));
        guest.setRole(Role.CLIENT);

        userRepository.saveAll(Arrays.asList(admin, chef, serveur, client, guest));
        System.out.println("Users created.");

        // --- RESTAURANT TABLES ---
        TableRestaurant table1 = new TableRestaurant();
        table1.setNumeroTable(1);
        table1.setNombrePersonne(2);

        TableRestaurant table2 = new TableRestaurant();
        table2.setNumeroTable(2);
        table2.setNombrePersonne(4);

        TableRestaurant table3 = new TableRestaurant();
        table3.setNumeroTable(3);
        table3.setNombrePersonne(4);

        TableRestaurant table4 = new TableRestaurant();
        table4.setNumeroTable(4);
        table4.setNombrePersonne(6);

        tableRepository.saveAll(Arrays.asList(table1, table2, table3, table4));
        System.out.println("Tables created.");

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
        menuDuJour.setDescription("salade vert et pizza Margherita");
        menuDuJour.setPrix(24.00);
        menuDuJour.setPlats(new HashSet<>(Arrays.asList(saladeVerte, pizzaMargherita)));
        menuRepository.save(menuDuJour);

        System.out.println("Test data loaded successfully!");
    }
}
