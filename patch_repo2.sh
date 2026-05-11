cat << 'PATCH_EOF' > /tmp/repo2.patch
--- src/main/java/com/example/demo/repository/TableRestaurantRepository.java
+++ src/main/java/com/example/demo/repository/TableRestaurantRepository.java
@@ -20,7 +20,7 @@
     List<TableRestaurant> findByStatutAndNombrePersonneGreaterThanEqual(StatutTable statut, int nombrePersonne);

     @Query("SELECT t FROM TableRestaurant t WHERE t NOT IN (" +
-           "SELECT c.table FROM Commande c JOIN c.lignesCommande lc " +
+           "SELECT c.table FROM Commande c JOIN c.lignesCommande lc WHERE c.table IS NOT NULL AND " +
-           "WHERE lc.etat IN (com.example.demo.model.EtatLigneCommande.EN_ATTENTE, " +
+           "lc.etat IN (com.example.demo.model.EtatLigneCommande.EN_ATTENTE, " +
            "com.example.demo.model.EtatLigneCommande.EN_PREPARATION, com.example.demo.model.EtatLigneCommande.SERVIE))")
     List<TableRestaurant> findAvailableTablesNotInActiveOrders();
 }
PATCH_EOF
patch src/main/java/com/example/demo/repository/TableRestaurantRepository.java < /tmp/repo2.patch
