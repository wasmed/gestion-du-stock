package com.example.demo.repository;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    List<Commande> findByEtat(EtatCommande etat);

    List<Commande> findByEtatIn(List<EtatCommande> etats);

    @Query("SELECT c FROM Commande c JOIN FETCH c.lignesCommande WHERE c.id = :identifiant")
    Optional<Commande> findByIdentifiantWithLignesCommande(Long identifiant);

    List<Commande> findByClientIdOrderByDateHeureDesc(Long clientId);

    List<Commande> findByClientIdOrderByDateHeureDesc(Long clientId, Pageable pageable);

    // Cherche la commande la plus récente d'un client dont l'état n'est PAS 'PAYEE'
    @Query("SELECT c FROM Commande c WHERE c.client.id = ?1 AND c.etat != 'PAYEE' ORDER BY c.dateHeure DESC LIMIT 1")
    Optional<Commande> findActiveCommandeByClientId(Long clientId);

    @Query("SELECT c FROM Commande c LEFT JOIN FETCH c.lignesCommande lc LEFT JOIN FETCH lc.plat LEFT JOIN FETCH lc.menu WHERE c.client = :client ORDER BY c.dateHeure DESC")
    List<Commande> findByClientWithLignesCommande(@Param("client") User client);

    @Query("SELECT DISTINCT c FROM Commande c LEFT JOIN FETCH c.lignesCommande WHERE c.etat IN :etats")
    List<Commande> findByEtatInWithDetails(@Param("etats") List<EtatCommande> etats);



    @Query("SELECT COUNT(c) FROM Commande c WHERE c.dateHeure >= :startDate")
    long countByDateHeureAfter(@Param("startDate") java.time.LocalDateTime startDate);

    @Query("SELECT SUM(c.montantTotal) FROM Commande c WHERE c.dateHeure >= :startDate AND c.etat = 'PAYEE'")
    Double sumTotalForToday(@Param("startDate") java.time.LocalDateTime startDate);
}

