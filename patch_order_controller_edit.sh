cat << 'PATCH_EOF' > /tmp/order_controller_edit.patch
--- src/main/java/com/example/demo/controller/OrderManagementController.java
+++ src/main/java/com/example/demo/controller/OrderManagementController.java
@@ -10,6 +10,7 @@
 import java.security.Principal;
 import java.util.List;
 import java.util.Map;
+import java.util.Optional;
 import java.util.stream.Collectors;

 @Controller
@@ -406,14 +407,24 @@
                         Long platId = Long.parseLong(key.substring(8));
                         Plat plat = platService.findPlatById(platId);
                         if (plat != null) {
-                            LigneCommande ligne = new LigneCommande();
-                            ligne.setCommande(commande);
-                            ligne.setPlat(plat);
-                            ligne.setQuantite(quantite);
-                            ligne.setTypeLigne(TypeLigneCommande.PLAT);
-                            ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
+                            Optional<LigneCommande> existingLigne = commande.getLignesCommande().stream()
+                                    .filter(l -> l.getPlat() != null && l.getPlat().getIdentifiant().equals(plat.getIdentifiant()) && l.getEtat() == EtatLigneCommande.EN_ATTENTE)
+                                    .findFirst();
+
+                            LigneCommande ligne;
+                            if (existingLigne.isPresent()) {
+                                ligne = existingLigne.get();
+                                ligne.setQuantite(ligne.getQuantite() + quantite);
+                            } else {
+                                ligne = new LigneCommande();
+                                ligne.setCommande(commande);
+                                ligne.setPlat(plat);
+                                ligne.setQuantite(quantite);
+                                ligne.setTypeLigne(TypeLigneCommande.PLAT);
+                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
+                                commande.getLignesCommande().add(ligne);
+                            }
                             montantTotal += plat.getPrix() * quantite;
                             ligne = ligneCommandeRepository.save(ligne);
                             stockService.processStockDecrementForLigne(ligne);
-                            commande.getLignesCommande().add(ligne); // <-- LA LIGNE CLÉ À AJOUTER
                         }
                     } else if (key.startsWith("menuQty_")) {
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
patch src/main/java/com/example/demo/controller/OrderManagementController.java < /tmp/order_controller_edit.patch
