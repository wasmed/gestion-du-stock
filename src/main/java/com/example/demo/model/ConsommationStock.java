package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class ConsommationStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifiant;

    private Double quantiteUtilisee;

    @ManyToOne
    @JoinColumn(name = "ligne_commande_id")
    private LigneCommande ligneCommande;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    public Long getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(Long identifiant) {
        this.identifiant = identifiant;
    }

    public Double getQuantiteUtilisee() {
        return quantiteUtilisee;
    }

    public void setQuantiteUtilisee(Double quantiteUtilisee) {
        this.quantiteUtilisee = quantiteUtilisee;
    }

    public LigneCommande getLigneCommande() {
        return ligneCommande;
    }

    public void setLigneCommande(LigneCommande ligneCommande) {
        this.ligneCommande = ligneCommande;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
}
