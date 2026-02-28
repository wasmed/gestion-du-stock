package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifiant;

    private Double quantite;
        @ManyToOne
    @JoinColumn(name = "format_produit_id")
    private FormatProduit formatProduit;

    @ManyToOne
    @JoinColumn(name = "plat_id")
    private Plat plat;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    public Long getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(Long identifiant) {
        this.identifiant = identifiant;
    }

    public Double getQuantite() {
        return quantite;
    }

    public void setQuantite(Double quantite) {
        this.quantite = quantite;
    }

    public FormatProduit getFormatProduit() {
        return formatProduit;
    }

    public void setFormatProduit(FormatProduit formatProduit) {
        this.formatProduit = formatProduit;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Plat getPlat() {
        return plat;
    }

    public void setPlat(Plat plat) {
        this.plat = plat;
    }
}
