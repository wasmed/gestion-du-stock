package com.example.demo;

import com.example.demo.model.*;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.LigneCommandeRepository;
import com.example.demo.repository.PlatRepository;
import com.example.demo.repository.UserRepository;
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
            CommandeRepository commandeRepository,
            LigneCommandeRepository ligneCommandeRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Créer des utilisateurs de test
           User client = new User();
            client.setEmail("testclient@example.com");
            client.setNom("Test Client");
            client.setMotDePasse(passwordEncoder.encode("password"));
            client.setRole(Role.CLIENT);
            userRepository.save(client);

            User serveur = new User();
            serveur.setEmail("serveur2@restaurant.com");
            serveur.setNom("Serveur Test");
            serveur.setMotDePasse(passwordEncoder.encode("password"));
            serveur.setRole(Role.SERVEUR);
            userRepository.save(serveur);

            // 2. Créer des plats de test
            Plat plat1 = new Plat();
            plat1.setNom("Pizza Margherita");
            plat1.setPrix(10.50);
            plat1.setCategorie(CategoriePlat.PLAT_PRINCIPAL);
            platRepository.save(plat1);

            Plat plat2 = new Plat();
            plat2.setNom("Tiramisu");
            plat2.setPrix(6.00);
            plat2.setCategorie(CategoriePlat.DESSERT);
            platRepository.save(plat2);

            // 3. Créer une commande de test
            Commande commande1 = new Commande();
            commande1.setClient(client);
            commande1.setServeur(serveur);
            commande1.setEtat(EtatCommande.EN_ATTENTE);
            commande1.setDateHeure(LocalDateTime.now());
            commande1.setMontantTotal(16.50);
            commandeRepository.save(commande1);

            // 4. Créer des lignes de commande pour la commande de test
            LigneCommande ligne1 = new LigneCommande();
            ligne1.setCommande(commande1);
            ligne1.setPlat(plat1);
            ligne1.setQuantite(1);
            ligne1.setTypeLigne(TypeLigneCommande.PLAT);
            ligneCommandeRepository.save(ligne1);

            LigneCommande ligne2 = new LigneCommande();
            ligne2.setCommande(commande1);
            ligne2.setPlat(plat2);
            ligne2.setQuantite(1);
            ligne2.setTypeLigne(TypeLigneCommande.PLAT);
            ligneCommandeRepository.save(ligne2);
        };
    }*/
}
