package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final PlatRepository platRepository;
    private final MenuRepository menuRepository;
    private final ProduitRepository produitRepository;
    private final IngredientRepository ingredientRepository;
    private final StockProduitRepository stockProduitRepository;
    private final UserRepository userRepository;
    private final TableRestaurantRepository tableRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(PlatRepository platRepository, MenuRepository menuRepository,
                      ProduitRepository produitRepository, IngredientRepository ingredientRepository,
                      StockProduitRepository stockProduitRepository, UserRepository userRepository,
                      TableRestaurantRepository tableRepository, PasswordEncoder passwordEncoder) {
        this.platRepository = platRepository;
        this.menuRepository = menuRepository;
        this.produitRepository = produitRepository;
        this.ingredientRepository = ingredientRepository;
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

        User serveur2 = new User();
        serveur2.setFullName("Serveur Deux");
        serveur2.setEmail("serveur2@resto.com");
        serveur2.setPassword(passwordEncoder.encode("serveur123"));
        serveur2.setRole(Role.SERVEUR);

        User client = new User();
        client.setFullName("Jean Dupont");
        client.setEmail("client@resto.com");
        client.setPassword(passwordEncoder.encode("client123"));
        client.setRole(Role.CLIENT);

        User guest = new User();
        guest.setFullName("Client Invité");
        guest.setEmail("guest@resto.com");
        guest.setPassword(passwordEncoder.encode("guest123"));
        guest.setRole(Role.CLIENT);

        userRepository.saveAll(Arrays.asList(admin, chef, serveur, serveur2, client, guest));
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
        table3.setNombrePersonne(6);

        TableRestaurant table4 = new TableRestaurant();
        table4.setNumeroTable(4);
        table4.setNombrePersonne(8);

        tableRepository.saveAll(Arrays.asList(table1, table2, table3, table4));
        System.out.println("Tables created.");

        // =================================================================
        // 1. CREATE PRODUCTS (Mis à jour avec quantite et unite)
        // =================================================================
        Produit tomate = new Produit();
        tomate.setNom("Tomate");
        tomate.setType(TypeProduit.LEGUME);
        tomate.setQuantite(1.0);
        tomate.setUnite("kg");
        tomate.setImage("https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500&q=80");

        Produit salade = new Produit();
        salade.setNom("Salade Laitue");
        salade.setType(TypeProduit.LEGUME);
        salade.setQuantite(1.0);
        salade.setUnite("Pièce");
        salade.setImage("https://images.unsplash.com/photo-1556801712-67c8c279a093?w=500&q=80");

        Produit patePizza = new Produit();
        patePizza.setNom("Pâte à Pizza");
        patePizza.setType(TypeProduit.BOULANGERIE);
        patePizza.setQuantite(5.0);
        patePizza.setUnite("kg");
        patePizza.setImage("https://images.unsplash.com/photo-1598155523122-38423bb4d6c1?w=500&q=80");

        Produit mozzarella = new Produit();
        mozzarella.setNom("Mozzarella");
        mozzarella.setType(TypeProduit.CREMERIE);
        mozzarella.setQuantite(1.0);
        mozzarella.setUnite("kg");
        mozzarella.setImage("https://images.unsplash.com/photo-1588612571212-320d7ee82596?w=500&q=80");

        Produit eauPlateProduit = new Produit();
        eauPlateProduit.setNom("Eau Plate 50cl");
        eauPlateProduit.setType(TypeProduit.BOISSON);
        eauPlateProduit.setQuantite(1.0);
        eauPlateProduit.setUnite("Bouteille");
        eauPlateProduit.setImage("https://images.unsplash.com/photo-1564419320461-6870880221ad?w=500&q=80");

        Produit chocolatNoir = new Produit();
        chocolatNoir.setNom("Chocolat Noir 70%");
        chocolatNoir.setType(TypeProduit.DESSERT);
        chocolatNoir.setQuantite(1.0);
        chocolatNoir.setUnite("kg");
        chocolatNoir.setImage("https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=500&q=80");

        Produit penne = new Produit();
        penne.setNom("Pâtes Penne");
        penne.setType(TypeProduit.EPICERIE_SECHE);
        penne.setQuantite(5.0);
        penne.setUnite("kg");
        penne.setImage("https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445?w=500&q=80");

        Produit cremeGlacee = new Produit();
        cremeGlacee.setNom("Crème Glacée Vanille");
        cremeGlacee.setType(TypeProduit.DESSERT);
        cremeGlacee.setQuantite(2.5);
        cremeGlacee.setUnite("Litre");
        cremeGlacee.setImage("https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=500&q=80");

        Produit mascarpone = new Produit();
        mascarpone.setNom("Mascarpone");
        mascarpone.setType(TypeProduit.CREMERIE);
        mascarpone.setQuantite(1.0);
        mascarpone.setUnite("kg");
        mascarpone.setImage("https://images.unsplash.com/photo-1585671720293-1994627d7045?w=500&q=80");

        Produit cafe = new Produit();
        cafe.setNom("Café Grain");
        cafe.setType(TypeProduit.BOISSON);
        cafe.setQuantite(1.0);
        cafe.setUnite("kg");
        cafe.setImage("https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=500&q=80");

        Produit boeuf = new Produit();
        boeuf.setNom("Steak Haché de Boeuf");
        boeuf.setType(TypeProduit.VIANDE);
        boeuf.setQuantite(1.0);
        boeuf.setUnite("kg");
        boeuf.setImage("https://images.unsplash.com/photo-1551028150-64b9f398f678?w=500&q=80");

        Produit saumon = new Produit();
        saumon.setNom("Filet de Saumon");
        saumon.setType(TypeProduit.POISSON);
        saumon.setQuantite(1.0);
        saumon.setUnite("kg");
        saumon.setImage("https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=500&q=80");

        Produit pomme = new Produit();
        pomme.setNom("Pomme");
        pomme.setType(TypeProduit.FRUIT);
        pomme.setQuantite(1.0);
        pomme.setUnite("kg");
        pomme.setImage("https://images.unsplash.com/photo-1560806887-1e4cd0b6fac6?w=500&q=80");

        Produit emballage = new Produit();
        emballage.setNom("Boîte à Pizza");
        emballage.setType(TypeProduit.AUTRE);
        emballage.setQuantite(100.0);
        emballage.setUnite("Pièces");
        emballage.setImage("https://images.unsplash.com/photo-1587280510058-fdb9bc2d7877?w=500&q=80");

        // Save all products first to get their IDs
        List<Produit> produits = Arrays.asList(tomate, salade, patePizza, mozzarella, eauPlateProduit, chocolatNoir, penne, cremeGlacee, mascarpone, cafe, boeuf, saumon, pomme, emballage);
        produitRepository.saveAll(produits);

        // =================================================================
        // 2. CREATE THE STOCK ASSOCIATED WITH EACH PRODUCT
        // =================================================================
        StockProduit stockTomate = new StockProduit();
        stockTomate.setProduit(tomate);
        stockTomate.setStockActuel(50.0);
        stockTomate.setStockMinimum(10.0);

        StockProduit stockSalade = new StockProduit();
        stockSalade.setProduit(salade);
        stockSalade.setStockActuel(20.0);
        stockSalade.setStockMinimum(5.0);

        StockProduit stockPatePizza = new StockProduit();
        stockPatePizza.setProduit(patePizza);
        stockPatePizza.setStockActuel(20.0);
        stockPatePizza.setStockMinimum(4.0);

        StockProduit stockMozzarella = new StockProduit();
        stockMozzarella.setProduit(mozzarella);
        stockMozzarella.setStockActuel(15.0);
        stockMozzarella.setStockMinimum(3.0);

        StockProduit stockEau = new StockProduit();
        stockEau.setProduit(eauPlateProduit);
        stockEau.setStockActuel(100.0);
        stockEau.setStockMinimum(24.0);

        StockProduit stockChocolat = new StockProduit();
        stockChocolat.setProduit(chocolatNoir);
        stockChocolat.setStockActuel(10.0);
        stockChocolat.setStockMinimum(2.0);

        StockProduit stockPenne = new StockProduit();
        stockPenne.setProduit(penne);
        stockPenne.setStockActuel(30.0);
        stockPenne.setStockMinimum(5.0);

        StockProduit stockGlace = new StockProduit();
        stockGlace.setProduit(cremeGlacee);
        stockGlace.setStockActuel(10.0);
        stockGlace.setStockMinimum(2.0);

        StockProduit stockMascarpone = new StockProduit();
        stockMascarpone.setProduit(mascarpone);
        stockMascarpone.setStockActuel(8.0);
        stockMascarpone.setStockMinimum(2.0);

        StockProduit stockCafe = new StockProduit();
        stockCafe.setProduit(cafe);
        stockCafe.setStockActuel(5.0);
        stockCafe.setStockMinimum(1.0);

        StockProduit stockBoeuf = new StockProduit();
        stockBoeuf.setProduit(boeuf);
        stockBoeuf.setStockActuel(10.0);
        stockBoeuf.setStockMinimum(2.0);

        StockProduit stockSaumon = new StockProduit();
        stockSaumon.setProduit(saumon);
        stockSaumon.setStockActuel(5.0);
        stockSaumon.setStockMinimum(1.0);

        StockProduit stockPomme = new StockProduit();
        stockPomme.setProduit(pomme);
        stockPomme.setStockActuel(15.0);
        stockPomme.setStockMinimum(3.0);

        StockProduit stockEmballage = new StockProduit();
        stockEmballage.setProduit(emballage);
        stockEmballage.setStockActuel(100.0);
        stockEmballage.setStockMinimum(20.0);

        List<StockProduit> stocks = Arrays.asList(stockTomate, stockSalade, stockPatePizza, stockMozzarella, stockEau, stockChocolat, stockPenne, stockGlace, stockMascarpone, stockCafe, stockBoeuf, stockSaumon, stockPomme, stockEmballage);
        stockProduitRepository.saveAll(stocks);

        // =================================================================
        // 3. CREATE DISHES (which use the products as ingredients)
        // =================================================================
        Plat saladeVerte = new Plat();
        saladeVerte.setNom("Salade Verte Simple");
        saladeVerte.setDescription("Une salade fraîche de saison avec vinaigrette balsamique.");
        saladeVerte.setPrix(7.50);
        saladeVerte.setCategorie(CategoriePlat.ENTREE);
        saladeVerte.setImage("https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500&q=80");
        platRepository.save(saladeVerte);

        Ingredient ingSalade = new Ingredient();
        ingSalade.setPlat(saladeVerte);
        ingSalade.setProduit(salade);
        ingSalade.setQuantite(0.150);
        ingredientRepository.save(ingSalade);

        Ingredient ingTomateSalade = new Ingredient();
        ingTomateSalade.setPlat(saladeVerte);
        ingTomateSalade.setProduit(tomate);
        ingTomateSalade.setQuantite(0.080);
        ingredientRepository.save(ingTomateSalade);

        Plat pizzaMargherita = new Plat();
        pizzaMargherita.setNom("Pizza Margherita");
        pizzaMargherita.setDescription("Sauce tomate, mozzarella fondante et basilic frais sur pâte fine.");
        pizzaMargherita.setPrix(12.00);
        pizzaMargherita.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
        pizzaMargherita.setImage("https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=500&q=80");
        platRepository.save(pizzaMargherita);

        Ingredient ingPate = new Ingredient();
        ingPate.setPlat(pizzaMargherita);
        ingPate.setProduit(patePizza);
        ingPate.setQuantite(0.250);
        ingredientRepository.save(ingPate);

        Ingredient ingMozzaPizza = new Ingredient();
        ingMozzaPizza.setPlat(pizzaMargherita);
        ingMozzaPizza.setProduit(mozzarella);
        ingMozzaPizza.setQuantite(0.120);
        ingredientRepository.save(ingMozzaPizza);

        Ingredient ingTomatePizza = new Ingredient();
        ingTomatePizza.setPlat(pizzaMargherita);
        ingTomatePizza.setProduit(tomate);
        ingTomatePizza.setQuantite(0.100);
        ingredientRepository.save(ingTomatePizza);

        Plat boissonEau = new Plat();
        boissonEau.setNom("Eau Plate");
        boissonEau.setDescription("Bouteille d'eau minérale de 50cl.");
        boissonEau.setPrix(3.00);
        boissonEau.setCategorie(CategoriePlat.BOISSON);
        boissonEau.setImage("https://images.unsplash.com/photo-1548839140-29a749e1cf4d?w=500&q=80");
        platRepository.save(boissonEau);

        Ingredient ingEau = new Ingredient();
        ingEau.setPlat(boissonEau);
        ingEau.setProduit(eauPlateProduit);
        ingEau.setQuantite(1.0);
        ingredientRepository.save(ingEau);

        // NEW PLATS
        Plat patesTomate = new Plat();
        patesTomate.setNom("Pennes à la Tomate");
        patesTomate.setDescription("Pâtes penne avec une sauce tomate maison et basilic.");
        patesTomate.setPrix(11.00);
        patesTomate.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
        patesTomate.setImage("https://images.unsplash.com/photo-1563379926898-05f4575a45d8?w=500&q=80");
        platRepository.save(patesTomate);

        Ingredient ingPenne = new Ingredient();
        ingPenne.setPlat(patesTomate);
        ingPenne.setProduit(penne);
        ingPenne.setQuantite(0.200);
        ingredientRepository.save(ingPenne);

        Ingredient ingTomatePenne = new Ingredient();
        ingTomatePenne.setPlat(patesTomate);
        ingTomatePenne.setProduit(tomate);
        ingTomatePenne.setQuantite(0.150);
        ingredientRepository.save(ingTomatePenne);

        Plat tiramisu = new Plat();
        tiramisu.setNom("Tiramisu Maison");
        tiramisu.setDescription("Le classique italien au mascarpone et café.");
        tiramisu.setPrix(6.50);
        tiramisu.setCategorie(CategoriePlat.DESSERT);
        tiramisu.setImage("https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=500&q=80");
        platRepository.save(tiramisu);

        Ingredient ingMascarpone = new Ingredient();
        ingMascarpone.setPlat(tiramisu);
        ingMascarpone.setProduit(mascarpone);
        ingMascarpone.setQuantite(0.100);
        ingredientRepository.save(ingMascarpone);

        Ingredient ingCafe = new Ingredient();
        ingCafe.setPlat(tiramisu);
        ingCafe.setProduit(cafe);
        ingCafe.setQuantite(0.020);
        ingredientRepository.save(ingCafe);

        Ingredient ingChocolat = new Ingredient();
        ingChocolat.setPlat(tiramisu);
        ingChocolat.setProduit(chocolatNoir);
        ingChocolat.setQuantite(0.010);
        ingredientRepository.save(ingChocolat);

        Plat glaceVanille = new Plat();
        glaceVanille.setNom("Coupe Glace Vanille");
        glaceVanille.setDescription("Deux boules de glace vanille artisanale.");
        glaceVanille.setPrix(5.00);
        glaceVanille.setCategorie(CategoriePlat.DESSERT);
        glaceVanille.setImage("https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500&q=80");
        platRepository.save(glaceVanille);

        Ingredient ingGlace = new Ingredient();
        ingGlace.setPlat(glaceVanille);
        ingGlace.setProduit(cremeGlacee);
        ingGlace.setQuantite(0.150);
        ingredientRepository.save(ingGlace);

        // =================================================================
        // 4. CREATE MENUS (which group dishes)
        // =================================================================
        Menu menuDuJour = new Menu();
        menuDuJour.setNom("Menu Italien");
        menuDuJour.setDescription("Un voyage en Italie avec salade et pizza.");
        menuDuJour.setPrix(18.50);
        menuDuJour.setActif(true);
        menuDuJour.setPlats(new HashSet<>(Arrays.asList(saladeVerte, pizzaMargherita)));
        menuDuJour.setImage("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&q=80");
        menuRepository.save(menuDuJour);

        Menu menuEnfant = new Menu();
        menuEnfant.setNom("Menu Enfant");
        menuEnfant.setDescription("Pâtes à la tomate et une glace pour les petits.");
        menuEnfant.setPrix(14.00);
        menuEnfant.setActif(true);
        menuEnfant.setPlats(new HashSet<>(Arrays.asList(patesTomate, glaceVanille)));
        menuEnfant.setImage("https://images.unsplash.com/photo-1621996346565-e3dbc646d9a9?w=500&q=80");
        menuRepository.save(menuEnfant);

        Menu menuGourmand = new Menu();
        menuGourmand.setNom("Menu Gourmand");
        menuGourmand.setDescription("Pizza et Tiramisu pour les gourmands.");
        menuGourmand.setPrix(17.50);
        menuGourmand.setActif(true);
        menuGourmand.setPlats(new HashSet<>(Arrays.asList(pizzaMargherita, tiramisu)));
        menuGourmand.setImage("https://images.unsplash.com/photo-1590947132387-155cc02f3212?w=500&q=80");
        menuRepository.save(menuGourmand);

        Menu menuSaintValentin = new Menu();
        menuSaintValentin.setNom("Menu Saint-Valentin");
        menuSaintValentin.setDescription("Menu exceptionnel pour la Saint-Valentin.");
        menuSaintValentin.setPrix(45.00);
        menuSaintValentin.setActif(true);
        menuSaintValentin.setDateDebut(LocalDate.now().minusDays(1));
        menuSaintValentin.setDateFin(LocalDate.now().plusDays(2));
        menuSaintValentin.setPlats(new HashSet<>(Arrays.asList(pizzaMargherita, tiramisu)));
        menuSaintValentin.setImage("https://images.unsplash.com/photo-1590947132387-155cc02f3212?w=500&q=80");
        menuRepository.save(menuSaintValentin);

        System.out.println("Test data loaded successfully!");
    }
}
