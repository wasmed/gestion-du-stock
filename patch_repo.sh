cat << 'PATCH_EOF' > /tmp/repo.patch
--- src/main/java/com/example/demo/repository/TableRestaurantRepository.java
+++ src/main/java/com/example/demo/repository/TableRestaurantRepository.java
@@ -4,6 +4,7 @@
 import com.example.demo.model.StatutTable;
 import com.example.demo.model.TableRestaurant;
 import org.springframework.data.jpa.repository.JpaRepository;
+import org.springframework.data.jpa.repository.Query;
 import org.springframework.stereotype.Repository;

 import java.util.List;
@@ -17,4 +18,10 @@

     // Cherche les tables libres avec une certaine capacité
     List<TableRestaurant> findByStatutAndNombrePersonneGreaterThanEqual(StatutTable statut, int nombrePersonne);
+
+    @Query("SELECT t FROM TableRestaurant t WHERE t NOT IN (" +
+           "SELECT c.table FROM Commande c JOIN c.lignesCommande lc " +
+           "WHERE lc.etat IN (com.example.demo.model.EtatLigneCommande.EN_ATTENTE, " +
+           "com.example.demo.model.EtatLigneCommande.EN_PREPARATION, com.example.demo.model.EtatLigneCommande.SERVIE))")
+    List<TableRestaurant> findAvailableTablesNotInActiveOrders();
 }
PATCH_EOF
patch src/main/java/com/example/demo/repository/TableRestaurantRepository.java < /tmp/repo.patch
