package com.example.demo.repository;

import com.example.demo.dto.ItemStatDTO;
import com.example.demo.model.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.plat.identifiant, lc.plat.nom, SUM(lc.quantite)) " +
           "FROM LigneCommande lc WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.PLAT " +
           "GROUP BY lc.plat.identifiant, lc.plat.nom ORDER BY SUM(lc.quantite) DESC")
    List<ItemStatDTO> findTopSellingPlats();

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.plat.identifiant, lc.plat.nom, SUM(lc.quantite)) " +
           "FROM LigneCommande lc WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.PLAT " +
           "GROUP BY lc.plat.identifiant, lc.plat.nom ORDER BY SUM(lc.quantite) ASC")
    List<ItemStatDTO> findBottomSellingPlats();

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.menu.id, lc.menu.nom, SUM(lc.quantite)) " +
           "FROM LigneCommande lc WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.MENU " +
           "GROUP BY lc.menu.id, lc.menu.nom ORDER BY SUM(lc.quantite) DESC")
    List<ItemStatDTO> findTopSellingMenus();

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.menu.id, lc.menu.nom, SUM(lc.quantite)) " +
           "FROM LigneCommande lc WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.MENU " +
           "GROUP BY lc.menu.id, lc.menu.nom ORDER BY SUM(lc.quantite) ASC")
    List<ItemStatDTO> findBottomSellingMenus();
}
