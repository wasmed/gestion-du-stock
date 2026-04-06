package com.example.demo.dto;

public class ItemStatDTO {
    private Long id;
    private String nom;
    private Double valeur;

    public ItemStatDTO(Long id, String nom, Double valeur) {
        this.id = id;
        this.nom = nom;
        this.valeur = valeur;
    }

    public ItemStatDTO(Long id, String nom, Long valeur) {
        this.id = id;
        this.nom = nom;
        this.valeur = valeur != null ? valeur.doubleValue() : null;
    }

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

    public Double getValeur() {
        return valeur;
    }

    public void setValeur(Double valeur) {
        this.valeur = valeur;
    }
}
