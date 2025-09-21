package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    private EtatCommande etat;

    private Double montantTotal;

    @ManyToOne
    @JoinColumn(name = "serveur_id")
    private User serveur;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @OneToMany(mappedBy = "commande")
    private Set<LigneCommande> lignesCommande;

    @OneToOne(mappedBy = "commande")
    private Paiement paiement;

    @ManyToOne
    @JoinColumn(name = "table_id")
    private TableRestaurant table;

    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private Feedback feedBack;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public Double getMontantTotal() {
        return montantTotal;
    }

    public void setMontantTotal(Double montantTotal) {
        this.montantTotal = montantTotal;
    }

    public User getServeur() {
        return serveur;
    }

    public void setServeur(User serveur) {
        this.serveur = serveur;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Set<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

    public void setLignesCommande(Set<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    public Paiement getPaiement() {
        return paiement;
    }

    public void setPaiement(Paiement paiement) {
        this.paiement = paiement;
    }

    public TableRestaurant getTable() {
        return table;
    }

    public void setTable(TableRestaurant table) {
        this.table = table;
    }

    public Feedback getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(Feedback feedBack) {
        this.feedBack = feedBack;
    }
}
