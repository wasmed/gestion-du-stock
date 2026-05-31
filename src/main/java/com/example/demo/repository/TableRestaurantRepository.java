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

    @Query("SELECT t FROM TableRestaurant t WHERE t.identifiant NOT IN (" +
           "SELECT c.table.identifiant FROM Commande c WHERE c.table IS NOT NULL AND c.etat IN ('EN_VALIDATION', 'EN_COURS'))")
    List<TableRestaurant> findAvailableTablesNotInActiveOrders();

    @Query("SELECT t FROM TableRestaurant t WHERE t.nombrePersonne >= :nombrePersonne AND t.identifiant NOT IN (" +
           "SELECT c.table.identifiant FROM Commande c WHERE c.table IS NOT NULL AND c.etat IN ('EN_VALIDATION', 'EN_COURS'))")
    List<TableRestaurant> findAvailableTablesNotInActiveOrdersWithCapacity(int nombrePersonne);
}
