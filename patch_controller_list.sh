cat << 'PATCH_EOF' > /tmp/controller_list.patch
--- src/main/java/com/example/demo/controller/OrderManagementController.java
+++ src/main/java/com/example/demo/controller/OrderManagementController.java
@@ -71,6 +71,8 @@
         List<Commande> commandesPretes = commandesEnCours.stream()
                 .filter(Commande::isPreteAServir)
                 .collect(Collectors.toList());
         model.addAttribute("commandesPretes", commandesPretes);
+
+        model.addAttribute("tablesDisponibles", tableRepository.findAvailableTablesNotInActiveOrders());

         return "serveur/dashboard";
     }
PATCH_EOF
patch src/main/java/com/example/demo/controller/OrderManagementController.java < /tmp/controller_list.patch
