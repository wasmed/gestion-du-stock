package com.example.demo.repository;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByEtat(EtatCommande etat);

    List<Commande> findByEtatIn(List<EtatCommande> etats);
}
