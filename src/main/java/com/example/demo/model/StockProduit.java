package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class StockProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double stockActuel;
    private Double stockMinimum;

    @OneToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(Double stockActuel) {
        this.stockActuel = stockActuel;
    }

    public Double getStockMinimum() {
        return stockMinimum;
    }

    public void setStockMinimum(Double stockMinimum) {
        this.stockMinimum = stockMinimum;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }

    public Integer getUnitesPleines() {
        if (this.produit == null || this.produit.getQuantite() == null || this.produit.getQuantite() == 0) return this.stockActuel != null ? this.stockActuel.intValue() : 0;
        Double quantiteTotale = this.stockActuel * this.produit.getQuantite();
        return (int) (quantiteTotale / this.produit.getQuantite());
    }

    public Double getResteEntame() {
        if (this.produit == null || this.produit.getQuantite() == null || this.produit.getQuantite() == 0) return 0.0;
        Double quantiteTotale = this.stockActuel * this.produit.getQuantite();
        return quantiteTotale % this.produit.getQuantite();
    }
}
