cat << 'PATCH_EOF' > /tmp/validate_endpoint.patch
--- src/main/java/com/example/demo/controller/OrderManagementController.java
+++ src/main/java/com/example/demo/controller/OrderManagementController.java
@@ -160,20 +160,24 @@
         return "redirect:/orders";
     }

-    @GetMapping("/validate/{id}")
+    @PostMapping("/validate/{id}")
     @PreAuthorize("hasRole('SERVEUR')")
-    public String validateOrder(@PathVariable Long id, Principal principal) {
+    public String validateOrder(@PathVariable Long id, @RequestParam Long tableId, Principal principal, RedirectAttributes redirectAttributes) {
         Commande commande = commandeService.findCommandeById(id);
         if (commande != null && commande.getEtat() == EtatCommande.EN_VALIDATION) {
             User serveur = userService.findUserByEmail(principal.getName());
             commande.setServeur(serveur);
-            commande.setEtat(EtatCommande.EN_COURS);
+            commande.setEtat(EtatCommande.EN_PREPARATION);

-            TableRestaurant table = commande.getTable();
+            TableRestaurant table = tableRepository.findById(tableId).orElse(null);
             if (table != null && table.getStatut() == StatutTable.LIBRE) {
                 table.setStatut(StatutTable.OCCUPEE);
                 table.setServeur(serveur);
                 tableRepository.save(table);
+            } else {
+                redirectAttributes.addFlashAttribute("errorMessage", "La table sélectionnée n'est pas disponible.");
+                return "redirect:/orders";
             }
+            commande.setTable(table);
             commandeService.saveCommande(commande);

             // Decrement stock for all lines since they are now validated and sent to kitchen
PATCH_EOF
patch src/main/java/com/example/demo/controller/OrderManagementController.java < /tmp/validate_endpoint.patch
