package com.example.demo.repository;

import com.example.demo.model.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    @Query("SELECT p FROM Produit p JOIN p.stockProduit sp WHERE sp.stockActuel <= sp.stockMinimum")
    List<Produit> findProduitsWithStockCritique();
}
