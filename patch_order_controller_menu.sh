cat << 'PATCH_EOF' > /tmp/order_controller_menu.patch
--- src/main/java/com/example/demo/controller/OrderManagementController.java
+++ src/main/java/com/example/demo/controller/OrderManagementController.java
@@ -432,15 +432,21 @@
                         Long menuId = Long.parseLong(key.substring(8));
                         Menu menu = menuService.findMenuById(menuId);
                         if (menu != null) {
-                            LigneCommande ligne = new LigneCommande();
-                            ligne.setCommande(commande);
-                            ligne.setMenu(menu);
-                            ligne.setQuantite(quantite);
-                            ligne.setTypeLigne(TypeLigneCommande.MENU);
-                            ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
+                            Optional<LigneCommande> existingLigne = commande.getLignesCommande().stream()
+                                    .filter(l -> l.getMenu() != null && l.getMenu().getId().equals(menu.getId()) && l.getEtat() == EtatLigneCommande.EN_ATTENTE)
+                                    .findFirst();
+
+                            LigneCommande ligne;
+                            if (existingLigne.isPresent()) {
+                                ligne = existingLigne.get();
+                                ligne.setQuantite(ligne.getQuantite() + quantite);
+                            } else {
+                                ligne = new LigneCommande();
+                                ligne.setCommande(commande);
+                                ligne.setMenu(menu);
+                                ligne.setQuantite(quantite);
+                                ligne.setTypeLigne(TypeLigneCommande.MENU);
+                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
+                                commande.getLignesCommande().add(ligne);
+                            }
                             montantTotal += menu.getPrix() * quantite;
                             ligne = ligneCommandeRepository.save(ligne);
                             stockService.processStockDecrementForLigne(ligne);
-                            commande.getLignesCommande().add(ligne); // <-- LA LIGNE CLÉ À AJOUTER
                         }
                     }
                 }
PATCH_EOF
patch src/main/java/com/example/demo/controller/OrderManagementController.java < /tmp/order_controller_menu.patch
