cat << 'PATCH_EOF' > /tmp/client_controller.patch
--- src/main/java/com/example/demo/controller/ClientController.java
+++ src/main/java/com/example/demo/controller/ClientController.java
@@ -238,15 +238,28 @@
             for (Map.Entry<Plat, Long> entry : groupedPlats.entrySet()) {
                 Plat plat = entry.getKey();
                 Long qty = entry.getValue();
-                LigneCommande ligne = new LigneCommande();
-                ligne.setCommande(commande);
-                ligne.setPlat(plat);
-                ligne.setQuantite(qty.intValue());
-                ligne.setTypeLigne(TypeLigneCommande.PLAT);
-                if (Boolean.TRUE.equals(isEmporter)) {
-                    ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
-                } else {
-                    ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
+
+                EtatLigneCommande targetEtat = Boolean.TRUE.equals(isEmporter) ? EtatLigneCommande.EN_ATTENTE : EtatLigneCommande.EN_VALIDATION;
+
+                Optional<LigneCommande> existingLigne = lignes.stream()
+                        .filter(l -> l.getPlat() != null && l.getPlat().getIdentifiant().equals(plat.getIdentifiant()) && l.getEtat() == targetEtat)
+                        .findFirst();
+
+                LigneCommande ligne;
+                if (existingLigne.isPresent()) {
+                    ligne = existingLigne.get();
+                    ligne.setQuantite(ligne.getQuantite() + qty.intValue());
+                } else {
+                    ligne = new LigneCommande();
+                    ligne.setCommande(commande);
+                    ligne.setPlat(plat);
+                    ligne.setQuantite(qty.intValue());
+                    ligne.setTypeLigne(TypeLigneCommande.PLAT);
+                    if (Boolean.TRUE.equals(isEmporter)) {
+                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
+                    } else {
+                        ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
+                    }
                 }
                 lignes.add(ligne);
                 montantTotal += plat.getPrix() * qty;
@@ -259,15 +272,28 @@
             for (Map.Entry<Menu, Long> entry : groupedMenus.entrySet()) {
                 Menu menu = entry.getKey();
                 Long qty = entry.getValue();
-                LigneCommande ligne = new LigneCommande();
-                ligne.setCommande(commande);
-                ligne.setMenu(menu);
-                ligne.setQuantite(qty.intValue());
-                ligne.setTypeLigne(TypeLigneCommande.MENU);
-                if (Boolean.TRUE.equals(isEmporter)) {
-                    ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
-                } else {
-                    ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
+
+                EtatLigneCommande targetEtat = Boolean.TRUE.equals(isEmporter) ? EtatLigneCommande.EN_ATTENTE : EtatLigneCommande.EN_VALIDATION;
+
+                Optional<LigneCommande> existingLigne = lignes.stream()
+                        .filter(l -> l.getMenu() != null && l.getMenu().getId().equals(menu.getId()) && l.getEtat() == targetEtat)
+                        .findFirst();
+
+                LigneCommande ligne;
+                if (existingLigne.isPresent()) {
+                    ligne = existingLigne.get();
+                    ligne.setQuantite(ligne.getQuantite() + qty.intValue());
+                } else {
+                    ligne = new LigneCommande();
+                    ligne.setCommande(commande);
+                    ligne.setMenu(menu);
+                    ligne.setQuantite(qty.intValue());
+                    ligne.setTypeLigne(TypeLigneCommande.MENU);
+                    if (Boolean.TRUE.equals(isEmporter)) {
+                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
+                    } else {
+                        ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
+                    }
                 }
                 lignes.add(ligne);
                 montantTotal += menu.getPrix() * qty;
PATCH_EOF
patch src/main/java/com/example/demo/controller/ClientController.java < /tmp/client_controller.patch
