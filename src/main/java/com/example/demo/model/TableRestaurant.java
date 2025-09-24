package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class TableRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long identifiant;

    private Integer numeroTable;

    private Integer nombrePersonne;

    @Enumerated(EnumType.STRING)
    private StatutTable statut;

    public TableRestaurant() {
        this.statut = StatutTable.LIBRE;
    }
    public Long getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(Long identifiant) {
        this.identifiant = identifiant;
    }

    public Integer getNumeroTable() {
        return numeroTable;
    }

    public void setNumeroTable(Integer numeroTable) {
        this.numeroTable = numeroTable;
    }

    public Integer getNombrePersonne() {
        return nombrePersonne;
    }

    public void setNombrePersonne(Integer nombrePersonne) {
        this.nombrePersonne = nombrePersonne;
    }

    public StatutTable getStatut() {
        return statut;
    }

    public void setStatut(StatutTable statut) {
        this.statut = statut;
    }
}
