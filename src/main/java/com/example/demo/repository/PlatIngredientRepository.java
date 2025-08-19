package com.example.demo.repository;

import com.example.demo.model.Plat;
import com.example.demo.model.PlatIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatIngredientRepository extends JpaRepository<PlatIngredient, Long> {

    List<PlatIngredient> findByPlat(Plat plat);
}
