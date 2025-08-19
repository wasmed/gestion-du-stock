package com.example.demo.repository;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByEtat(EtatCommande etat);

    List<Commande> findByEtatIn(List<EtatCommande> etats);

    @Query("SELECT c FROM Commande c JOIN FETCH c.lignesCommande WHERE c.id = :identifiant")
    Optional<Commande> findByIdentifiantWithLignesCommande(Long identifiant);
}
