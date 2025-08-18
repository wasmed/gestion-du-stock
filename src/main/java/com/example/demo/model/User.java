package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;


import java.util.Set;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String email;

    private String motDePasse;


    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "client")
    private Set<Commande> commandesClient;

    @OneToMany(mappedBy = "serveur")
    private Set<Commande> commandesServeur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Commande> getCommandesClient() {
        return commandesClient;
    }

    public void setCommandesClient(Set<Commande> commandesClient) {
        this.commandesClient = commandesClient;
    }

    public Set<Commande> getCommandesServeur() {
        return commandesServeur;
    }

    public void setCommandesServeur(Set<Commande> commandesServeur) {
        this.commandesServeur = commandesServeur;
    }
}
