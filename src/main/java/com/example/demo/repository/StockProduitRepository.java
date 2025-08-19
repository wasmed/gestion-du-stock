package com.example.demo.repository;

import com.example.demo.model.Produit;
import com.example.demo.model.StockProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockProduitRepository extends JpaRepository<StockProduit, Long> {

    StockProduit findByProduit(Produit produit);
}
