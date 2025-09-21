package com.example.demo.repository;

import com.example.demo.model.ConsommationStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsommationStockRepository  extends JpaRepository<ConsommationStock, Long> {
}
