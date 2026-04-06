package com.example.demo.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifiant;

    private String nom;
    private String description;
    private Double prix;

    private String image;

    @Enumerated(EnumType.STRING)
    private CategoriePlat categorie;

    @OneToMany(mappedBy = "plat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ingredient> ingredients = new ArrayList<>();

    @ManyToMany(mappedBy = "plats")
    private Set<Menu> menus;

    @Column(nullable = false)
    private Boolean actif = true;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public CategoriePlat getCategorie() {
        return categorie;
    }

    public void setCategorie(CategoriePlat categorie) {
        this.categorie = categorie;
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}
