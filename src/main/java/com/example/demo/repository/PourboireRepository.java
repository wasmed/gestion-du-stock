package com.example.demo.repository;


import com.example.demo.model.Pourboire;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PourboireRepository extends JpaRepository<Pourboire, Long> {
}
