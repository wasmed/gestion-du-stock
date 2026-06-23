package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Random;
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
    private final CommandeRepository commandeRepository;
    private FeedbackRepository feedbackRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(PlatRepository platRepository, MenuRepository menuRepository,
                      ProduitRepository produitRepository, IngredientRepository ingredientRepository,
                      StockProduitRepository stockProduitRepository, UserRepository userRepository,
                      TableRestaurantRepository tableRepository,CommandeRepository commandeRepository,FeedbackRepository feedbackRepository, PasswordEncoder passwordEncoder) {
        this.platRepository = platRepository;
        this.menuRepository = menuRepository;
        this.produitRepository = produitRepository;
        this.ingredientRepository = ingredientRepository;
        this.stockProduitRepository = stockProduitRepository;
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
        this.commandeRepository = commandeRepository;
        this.feedbackRepository = feedbackRepository;
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
        // =================================================================
        // --- USERS (Admin, Chef, 3 Serveurs, 6 Clients)
        // =================================================================
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

        User serveur1 = new User();
        serveur1.setFullName("Thomas Serveur");
        serveur1.setEmail("serveur1@resto.com");
        serveur1.setPassword(passwordEncoder.encode("serveur123"));
        serveur1.setRole(Role.SERVEUR);

        User serveur2 = new User();
        serveur2.setFullName("Marie Serveuse");
        serveur2.setEmail("serveur2@resto.com");
        serveur2.setPassword(passwordEncoder.encode("serveur123"));
        serveur2.setRole(Role.SERVEUR);

        User serveur3 = new User();
        serveur3.setFullName("Luc Serveur");
        serveur3.setEmail("serveur3@resto.com");
        serveur3.setPassword(passwordEncoder.encode("serveur123"));
        serveur3.setRole(Role.SERVEUR);

        // 6 Clients
        List<User> clients = Arrays.asList(
                createUser("Jean Dupont", "jean@resto.com", Role.CLIENT),
                createUser("Alice Martin", "alice@resto.com", Role.CLIENT),
                createUser("Bob L'éponge", "bob@resto.com", Role.CLIENT),
                createUser("Charlie Chaplin", "charlie@resto.com", Role.CLIENT),
                createUser("Samira Hammou", "samira@resto.com", Role.CLIENT),
                createUser("Client Invité", "guest@resto.com", Role.CLIENT)
        );

        userRepository.saveAll(Arrays.asList(admin, chef, serveur1, serveur2, serveur3));
        userRepository.saveAll(clients);
        System.out.println("Utilisateurs (Serveurs et Clients) créés.");

        // =================================================================
        // --- RESTAURANT TABLES (10 Tables)
        // =================================================================
        for (int i = 1; i <= 10; i++) {
            TableRestaurant table = new TableRestaurant();
            table.setNumeroTable(i);
            // Capacité aléatoire entre 2 et 8 personnes (chiffre pair)
            table.setNombrePersonne((i % 4 + 1) * 2);
            tableRepository.save(table);
        }
        List<TableRestaurant> allTables = tableRepository.findAll();
        System.out.println("10 Tables créées.");

        // =================================================================
        // 1 & 2. CREATE PRODUCTS & STOCKS
        // =================================================================
        Produit pommeTerre = produitRepository.save(createProduit("Pommes de terre (Frites)", TypeProduit.LEGUME, 20.0, "kg", "/assets/images/frites.jpg"));
        Produit boeuf = produitRepository.save(createProduit("Viande de Boeuf", TypeProduit.VIANDE, 15.0, "kg", "/assets/images/boeuf.jpg"));
        Produit biereBrune = produitRepository.save(createProduit("Bière d'Abbaye", TypeProduit.BOISSON, 50.0, "Bouteille", "/assets/images/biere.jpg"));
        Produit oignon = produitRepository.save(createProduit("Oignons", TypeProduit.LEGUME, 10.0, "kg", "/assets/images/oignon.jpg"));
        Produit pateGaufre = produitRepository.save(createProduit("Pâte à Gaufre", TypeProduit.BOULANGERIE, 5.0, "kg", "/assets/images/pate.jpg"));
        Produit chocolatNoir = produitRepository.save(createProduit("Chocolat Noir 70%", TypeProduit.DESSERT, 5.0, "kg", "/assets/images/chocolat.jpg"));
        Produit eauPlate = produitRepository.save(createProduit("Eau Plate 50cl", TypeProduit.BOISSON, 100.0, "Bouteille", "/assets/images/eau.jpg"));
        Produit limonade = produitRepository.save(createProduit("Limonade Artisanale", TypeProduit.BOISSON, 50.0, "Bouteille", "/assets/images/limonade.jpg"));
        Produit salade = produitRepository.save(createProduit("Mélange Salade", TypeProduit.LEGUME, 5.0, "kg", "/assets/images/salade.jpg"));
        Produit glaceVanille = produitRepository.save(createProduit("Glace Vanille", TypeProduit.DESSERT, 10.0, "Litre", "/assets/images/glace.jpg"));
        Produit fromage = produitRepository.save(createProduit("Fromage à croquettes", TypeProduit.CREMERIE, 8.0, "kg", "/assets/images/fromage.jpg"));

// On garde la liste pour faire la boucle des stocks juste après
        List<Produit> produits = Arrays.asList(pommeTerre, boeuf, biereBrune, oignon, pateGaufre, chocolatNoir, eauPlate, limonade, salade, glaceVanille, fromage);

        // Sauvegarde des stocks (simplifié pour l'exemple)
        for (Produit p : produits) {
            StockProduit stock = new StockProduit();
            stock.setProduit(p);
            stock.setStockActuel(p.getQuantite());
            stock.setStockMinimum(5.0);
            stockProduitRepository.save(stock);
        }

        // =================================================================
        // 3. CREATE DISHES (6 plats, 3 desserts, 3 boissons)
        // =================================================================
        Plat burgerMaison = platRepository.save(createPlat("Burger du Chef", "Steak haché pur boeuf, oignons caramélisés et frites.", 16.00, CategoriePlat.PLAT_PRINCIPAL, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500"));
        Plat boulets = platRepository.save(createPlat("Boulets à la Liégeoise", "Boulettes de viande sauce sirop de Liège et frites.", 15.50, CategoriePlat.PLAT_PRINCIPAL, "https://images.unsplash.com/photo-1529042410759-befb1204b468?w=500"));
        Plat saladeVerte = platRepository.save(createPlat("Salade Mixte", "Salade fraîche de saison avec vinaigrette balsamique.", 7.50, CategoriePlat.ENTREE, "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500"));

        Plat dameBlanche = platRepository.save(createPlat("Dame Blanche", "Glace vanille, sauce chocolat chaud.", 6.00, CategoriePlat.DESSERT, "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500"));

        Plat boissonEau = platRepository.save(createPlat("Eau Plate 50cl", "Bouteille d'eau minérale.", 2.50, CategoriePlat.BOISSON, "https://images.unsplash.com/photo-1548839140-29a749e1cf4d?w=500"));
        Plat boissonLimonade = platRepository.save(createPlat("Limonade", "Limonade artisanale au citron.", 3.50, CategoriePlat.BOISSON, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd?w=500"));
        Plat carbonnade = platRepository.save(createPlat("Carbonnade Flamande", "Mijoté de boeuf à la bière d'abbaye et frites maison.", 18.50, CategoriePlat.PLAT_PRINCIPAL, "https://loremflickr.com/500/500/beef,stew"));
        Plat croquettes = platRepository.save(createPlat("Croquettes au Fromage", "Duo de croquettes artisanales et persil frit.", 9.00, CategoriePlat.ENTREE, "https://loremflickr.com/500/500/croquette,cheese"));
        Plat carpaccio = platRepository.save(createPlat("Carpaccio de Boeuf", "Fines tranches de boeuf, parmesan et roquette.", 12.00, CategoriePlat.ENTREE, "https://loremflickr.com/500/500/carpaccio,beef"));

        Plat mousseChoco = platRepository.save(createPlat("Mousse au Chocolat", "Mousse onctueuse au chocolat noir.", 6.50, CategoriePlat.DESSERT, "https://loremflickr.com/500/500/chocolate,mousse"));
        Plat gaufre = platRepository.save(createPlat("Gaufre de Bruxelles", "Gaufre croustillante, sucre impalpable et chantilly.", 7.00, CategoriePlat.DESSERT, "https://loremflickr.com/500/500/waffle,dessert"));

        Plat boissonBiere = platRepository.save(createPlat("Bière d'Abbaye", "Bière pression 33cl.", 4.50, CategoriePlat.BOISSON, "https://loremflickr.com/500/500/beer,glass"));
        List<Plat> allPlats = Arrays.asList(carbonnade, burgerMaison, boulets, saladeVerte, croquettes, carpaccio, mousseChoco, gaufre, dameBlanche, boissonEau, boissonBiere, boissonLimonade);


        // =================================================================
        // 4. CREATE MENUS (3 Menus)
        // =================================================================
        Menu menuTerroir = new Menu();
        menuTerroir.setNom("Menu du Terroir");
        menuTerroir.setDescription("Entrée au choix, Plat traditionnel et Dessert.");
        menuTerroir.setPrix(32.00);
        menuTerroir.setActif(true);
        menuTerroir.setPlats(new HashSet<>(Arrays.asList(croquettes, carbonnade, mousseChoco)));
        menuTerroir.setImage("https://loremflickr.com/500/500/meat,traditional");
        menuRepository.save(menuTerroir);

        Menu menuBrasserie = new Menu();
        menuBrasserie.setNom("Menu Brasserie");
        menuBrasserie.setDescription("Le classique : Burger Maison et boisson au choix.");
        menuBrasserie.setPrix(19.00);
        menuBrasserie.setActif(true);
        menuBrasserie.setPlats(new HashSet<>(Arrays.asList(burgerMaison, boissonBiere)));
        menuBrasserie.setImage("https://loremflickr.com/500/500/burger,pub");
        menuRepository.save(menuBrasserie);

        Menu menuEnfant = new Menu();
        menuEnfant.setNom("Menu P'tit Chef");
        menuEnfant.setDescription("Boulets sauce tomate, boisson et glace.");
        menuEnfant.setPrix(12.00);
        menuEnfant.setActif(true);
        menuEnfant.setPlats(new HashSet<>(Arrays.asList(boulets, dameBlanche, boissonLimonade)));
        menuEnfant.setImage("https://loremflickr.com/500/500/meatballs,fries");
        menuRepository.save(menuEnfant);

        List<Menu> allMenus = Arrays.asList(menuTerroir, menuBrasserie, menuEnfant);

        // =================================================================
// 3.5 CREATE INGREDIENTS (Fiches techniques pour gestion de stock)
// =================================================================
        List<Ingredient> tousLesIngredients = Arrays.asList(
                // Carbonnade Flamande
                createIngredient(carbonnade, boeuf, 0.250),      // 250g de boeuf
                createIngredient(carbonnade, biereBrune, 1.0),   // 1 bouteille
                createIngredient(carbonnade, oignon, 0.100),     // 100g d'oignons
                createIngredient(carbonnade, pommeTerre, 0.300), // 300g de frites

                // Burger Maison
                createIngredient(burgerMaison, boeuf, 0.250),
                createIngredient(burgerMaison, oignon, 0.050),
                createIngredient(burgerMaison, pommeTerre, 0.300),

                // Boulets Liégeois
                createIngredient(boulets, boeuf, 0.300),
                createIngredient(boulets, pommeTerre, 0.300),

                // Entrées
                createIngredient(saladeVerte, salade, 0.150),
                createIngredient(croquettes, fromage, 0.150),
                createIngredient(carpaccio, boeuf, 0.150),
                createIngredient(carpaccio, salade, 0.050),

                // Desserts
                createIngredient(mousseChoco, chocolatNoir, 0.080),
                createIngredient(gaufre, pateGaufre, 0.150),
                createIngredient(dameBlanche, glaceVanille, 0.200), // 200ml de glace
                createIngredient(dameBlanche, chocolatNoir, 0.050), // 50g de chocolat fondu

                // Boissons (1 plat boisson = 1 produit déduit)
                createIngredient(boissonEau, eauPlate, 1.0),
                createIngredient(boissonBiere, biereBrune, 1.0),
                createIngredient(boissonLimonade, limonade, 1.0)
        );

        ingredientRepository.saveAll(tousLesIngredients);
        System.out.println("Ingrédients et fiches techniques générés avec succès.");

        // =================================================================
        // 5. GÉNÉRATION DE L'HISTORIQUE DES COMMANDES (30 pour les Stats)
        // =================================================================
        Random random = new Random();
        List<User> serveurs = Arrays.asList(serveur1, serveur2, serveur3);

        for (int i = 0; i < 30; i++) {
            Commande cmd = new Commande();
            cmd.setClient(clients.get(random.nextInt(clients.size())));
            cmd.setEtat(EtatCommande.PAYEE);

            // Répartir les commandes sur les 30 derniers jours pour des graphiques riches
            int daysAgo = random.nextInt(30);
            cmd.setDateHeure(LocalDateTime.now().minusDays(daysAgo).minusHours(random.nextInt(10)));

            boolean isEmporter = random.nextInt(10) > 7; // 30% à emporter
            cmd.setIsEmporter(isEmporter);

            if (!isEmporter) {
                cmd.setTable(allTables.get(random.nextInt(allTables.size())));
                cmd.setServeur(serveurs.get(random.nextInt(serveurs.size())));
            }

            double total = 0.0;
            int nbItems = random.nextInt(4) + 1; // 1 à 4 articles par commande

            for (int j = 0; j < nbItems; j++) {
                LigneCommande ligne = new LigneCommande();
                ligne.setCommande(cmd);
                ligne.setEtat(EtatLigneCommande.SERVIE);
                ligne.setQuantite(random.nextInt(2) + 1);

                if (random.nextInt(100) < 60) { // 60% Plats purs
                    Plat p = allPlats.get(random.nextInt(allPlats.size()));
                    ligne.setPlat(p);
                    ligne.setTypeLigne(TypeLigneCommande.PLAT);
                    total += p.getPrix() * ligne.getQuantite();
                } else { // 40% Menus
                    Menu m = allMenus.get(random.nextInt(allMenus.size()));
                    ligne.setMenu(m);
                    ligne.setTypeLigne(TypeLigneCommande.MENU);
                    total += m.getPrix() * ligne.getQuantite();
                }
                cmd.getLignesCommande().add(ligne);
            }

            cmd.setMontantTotal(total);
            commandeRepository.save(cmd);
        }
        System.out.println("30 Commandes historiques générées pour les statistiques !");

        // =================================================================
        // 6. GÉNÉRATION DES FEEDBACKS (Commentaires pour statistiques)
        // =================================================================


        List<String> commentairesPositifs = Arrays.asList(
            "Superbe expérience, la viande était excellente !",
            "Service rapide et serveur très souriant.",
            "Le meilleur restaurant du coin. Menu parfait.",
            "Cadre très agréable, je recommande la mousse au chocolat."
        );
        List<String> commentairesMoyens = Arrays.asList(
            "C'était bon mais un peu bruyant ce soir-là.",
            "Attente un peu longue, mais la qualité des plats rattrape tout."
        );

        // On récupère toutes les commandes qu'on vient de créer
        List<Commande> toutesLesCommandes = commandeRepository.findAll();

// On s'assure de ne pas créer plus de feedbacks qu'il n'y a de commandes disponibles
        int nombreDeFeedbacks = Math.min(15, toutesLesCommandes.size());

        for (int i = 0; i < nombreDeFeedbacks; i++) {
            Feedback avis = new Feedback();

            // Au lieu de l'aléatoire, on prend la commande à l'index 'i'.
            // Ainsi, la commande 0 a le feedback 0, la commande 1 a le feedback 1, etc. -> Aucune duplication !
            Commande commandeUnique = toutesLesCommandes.get(i);
            avis.setCommande(commandeUnique);

            // On peut récupérer le client directement depuis la commande pour que ce soit logique
            avis.setClient(commandeUnique.getClient());

            int note = random.nextInt(3) + 3; // Notes entre 3 et 5
            avis.setNote(note);

            if (note == 5) {
                avis.setCommentaire(commentairesPositifs.get(random.nextInt(commentairesPositifs.size())));
            } else {
                avis.setCommentaire(commentairesMoyens.get(random.nextInt(commentairesMoyens.size())));
            }

            // On met la date de l'avis juste après la date de la commande
            avis.setDateSubmitted(commandeUnique.getDateHeure().plusHours(random.nextInt(48)));

            feedbackRepository.save(avis);
        }
        System.out.println("Feedbacks générés !");


        System.out.println("=== TOUTES LES DONNÉES DE DÉMONSTRATION SONT CHARGÉES ===");
    }

    // --- Méthodes utilitaires pour alléger le code ---

    private User createUser(String fullName, String email, Role role) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123")); // Mot de passe standard
        user.setRole(role);
        return user;
    }

    private Produit createProduit(String nom, TypeProduit type, double quantite, String unite, String image) {
        Produit p = new Produit();
        p.setNom(nom);
        p.setType(type);
        p.setQuantite(quantite);
        p.setUnite(unite);
        p.setImage(image);
        return p;
    }

    private Plat createPlat(String nom, String description, double prix, CategoriePlat categorie, String image) {
        Plat plat = new Plat();
        plat.setNom(nom);
        plat.setDescription(description);
        plat.setPrix(prix);
        plat.setCategorie(categorie);
        plat.setImage(image);
        return plat;
    }

    private Ingredient createIngredient(Plat plat, Produit produit, double quantite) {
        Ingredient ingredient = new Ingredient();
        ingredient.setPlat(plat);
        ingredient.setProduit(produit);
        ingredient.setQuantite(quantite);
        return ingredient;
    }
}
