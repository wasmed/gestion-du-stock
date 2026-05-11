cat << 'PATCH_EOF' > /tmp/validate_endpoint2.patch
--- src/main/java/com/example/demo/controller/OrderManagementController.java
+++ src/main/java/com/example/demo/controller/OrderManagementController.java
@@ -160,22 +160,24 @@
         return "redirect:/orders";
     }

-    @PostMapping("/validate/{id}")
+    @RequestMapping(value = "/validate/{id}", method = {RequestMethod.GET, RequestMethod.POST})
     @PreAuthorize("hasRole('SERVEUR')")
-    public String validateOrder(@PathVariable Long id, @RequestParam Long tableId, Principal principal, RedirectAttributes redirectAttributes) {
+    public String validateOrder(@PathVariable Long id, @RequestParam(required = false) Long tableId, Principal principal, RedirectAttributes redirectAttributes) {
         Commande commande = commandeService.findCommandeById(id);
         if (commande != null && commande.getEtat() == EtatCommande.EN_VALIDATION) {
             User serveur = userService.findUserByEmail(principal.getName());
             commande.setServeur(serveur);
             commande.setEtat(EtatCommande.EN_PREPARATION);

-            TableRestaurant table = tableRepository.findById(tableId).orElse(null);
-            if (table != null && table.getStatut() == StatutTable.LIBRE) {
-                table.setStatut(StatutTable.OCCUPEE);
-                table.setServeur(serveur);
-                tableRepository.save(table);
-            } else {
-                redirectAttributes.addFlashAttribute("errorMessage", "La table sélectionnée n'est pas disponible.");
-                return "redirect:/orders";
-            }
-            commande.setTable(table);
+            if (!Boolean.TRUE.equals(commande.getIsEmporter()) && tableId != null) {
+                TableRestaurant table = tableRepository.findById(tableId).orElse(null);
+                if (table != null && table.getStatut() == StatutTable.LIBRE) {
+                    table.setStatut(StatutTable.OCCUPEE);
+                    table.setServeur(serveur);
+                    tableRepository.save(table);
+                    commande.setTable(table);
+                } else {
+                    redirectAttributes.addFlashAttribute("errorMessage", "La table sélectionnée n'est pas disponible.");
+                    return "redirect:/orders";
+                }
+            }
             commandeService.saveCommande(commande);

             // Decrement stock for all lines since they are now validated and sent to kitchen
PATCH_EOF
patch src/main/java/com/example/demo/controller/OrderManagementController.java < /tmp/validate_endpoint2.patch
