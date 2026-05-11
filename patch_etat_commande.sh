cat << 'PATCH_EOF' > /tmp/etat_commande.patch
--- src/main/java/com/example/demo/model/EtatCommande.java
+++ src/main/java/com/example/demo/model/EtatCommande.java
@@ -4,5 +4,6 @@

     EN_VALIDATION,
     EN_COURS,
+    EN_PREPARATION,
     PAYEE
 }
PATCH_EOF
patch src/main/java/com/example/demo/model/EtatCommande.java < /tmp/etat_commande.patch
