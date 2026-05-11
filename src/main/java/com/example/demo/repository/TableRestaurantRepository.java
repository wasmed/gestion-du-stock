package com.example.demo.repository;



import com.example.demo.model.StatutTable;
import com.example.demo.model.TableRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRestaurantRepository  extends JpaRepository<TableRestaurant, Long> {
    List<TableRestaurant> findByNombrePersonneGreaterThanEqual(Integer nombrePersonne);
    TableRestaurant findByIdentifiant(Long identifiant);

    // Cherche les tables libres
    List<TableRestaurant> findByStatut(StatutTable statut);

    // Cherche les tables libres avec une certaine capacité
    List<TableRestaurant> findByStatutAndNombrePersonneGreaterThanEqual(StatutTable statut, int nombrePersonne);

    @Query("SELECT t FROM TableRestaurant t WHERE t NOT IN (" +
           "SELECT c.table FROM Commande c JOIN c.lignesCommande lc WHERE c.table IS NOT NULL AND " +
           "lc.etat IN (com.example.demo.model.EtatLigneCommande.EN_ATTENTE, " +
           "com.example.demo.model.EtatLigneCommande.EN_PREPARATION, com.example.demo.model.EtatLigneCommande.SERVIE))")
    List<TableRestaurant> findAvailableTablesNotInActiveOrders();
}
