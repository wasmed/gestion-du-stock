package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class StockProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double stockActuel;
    private Double stockMinimum;
        @ManyToOne
    @JoinColumn(name = "format_produit_id")
    private FormatProduit formatProduit;

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
}
