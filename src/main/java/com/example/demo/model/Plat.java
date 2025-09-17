package com.example.demo.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifiant;

    private String nom;
    private String description;
    private Double prix;

    @Enumerated(EnumType.STRING)
    private CategoriePlat categorie;

    @OneToMany(mappedBy = "plat")
    private Set<PlatIngredient> platIngredients;

    @ManyToMany(mappedBy = "plats")
    private Set<Menu> menus;

    public Long getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(Long identifiant) {
        this.identifiant = identifiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrix() {
        return prix;
    }

    public void setPrix(Double prix) {
        this.prix = prix;
    }

    public CategoriePlat getCategorie() {
        return categorie;
    }

    public void setCategorie(CategoriePlat categorie) {
        this.categorie = categorie;
    }

    public Set<PlatIngredient> getPlatIngredients() {
        return platIngredients;
    }

    public void setPlatIngredients(Set<PlatIngredient> platIngredients) {
        this.platIngredients = platIngredients;
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }
}
