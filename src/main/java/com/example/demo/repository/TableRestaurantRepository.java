package com.example.demo.repository;



import com.example.demo.model.TableRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRestaurantRepository  extends JpaRepository<TableRestaurant, Long> {
    List<TableRestaurant> findByNombrePersonneGreaterThanEqual(Integer nombrePersonne);
    TableRestaurant findByIdentifiant(Long identifiant);
}
