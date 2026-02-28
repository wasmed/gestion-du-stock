package com.example.demo.repository;

import com.example.demo.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m WHERE m.actif = true AND (m.dateDebut IS NULL OR m.dateDebut <= :currentDate) AND (m.dateFin IS NULL OR m.dateFin >= :currentDate)")
    List<Menu> findActiveMenus(@Param("currentDate") LocalDate currentDate);
}
