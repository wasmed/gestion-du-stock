package com.example.demo.repository;



import com.example.demo.model.TableRestaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRestaurantRepository  extends JpaRepository<TableRestaurant, Long> {
}
