package com.example.demo.repository;

import com.example.demo.model.FormatProduit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatProduitRepository extends JpaRepository<FormatProduit, Long> {
}
