package com.example.demo.repository;

import com.example.demo.dto.ItemStatDTO;
import com.example.demo.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.plat.identifiant, lc.plat.nom, AVG(f.note)) " +
           "FROM Feedback f JOIN f.commande c JOIN c.lignesCommande lc " +
           "WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.PLAT AND lc.plat.categorie = :categorie " +
           "GROUP BY lc.plat.identifiant, lc.plat.nom ORDER BY AVG(f.note) DESC")
    List<ItemStatDTO> findTopRatedPlatsByCategory(@org.springframework.data.repository.query.Param("categorie") com.example.demo.model.CategoriePlat categorie);

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.plat.identifiant, lc.plat.nom, AVG(f.note)) " +
           "FROM Feedback f JOIN f.commande c JOIN c.lignesCommande lc " +
           "WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.PLAT AND lc.plat.categorie = :categorie " +
           "GROUP BY lc.plat.identifiant, lc.plat.nom ORDER BY AVG(f.note) ASC")
    List<ItemStatDTO> findBottomRatedPlatsByCategory(@org.springframework.data.repository.query.Param("categorie") com.example.demo.model.CategoriePlat categorie);

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.menu.id, lc.menu.nom, AVG(f.note)) " +
           "FROM Feedback f JOIN f.commande c JOIN c.lignesCommande lc " +
           "WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.MENU " +
           "GROUP BY lc.menu.id, lc.menu.nom ORDER BY AVG(f.note) DESC")
    List<ItemStatDTO> findTopRatedMenus();

    @Query("SELECT new com.example.demo.dto.ItemStatDTO(lc.menu.id, lc.menu.nom, AVG(f.note)) " +
           "FROM Feedback f JOIN f.commande c JOIN c.lignesCommande lc " +
           "WHERE lc.typeLigne = com.example.demo.model.TypeLigneCommande.MENU " +
           "GROUP BY lc.menu.id, lc.menu.nom ORDER BY AVG(f.note) ASC")
    List<ItemStatDTO> findBottomRatedMenus();
}
