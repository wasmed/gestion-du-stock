import sys

file_path = "src/main/java/com/example/demo/controller/ClientController.java"
with open(file_path, "r") as f:
    content = f.read()

search_plat = """                    if (Boolean.TRUE.equals(isEmporter)) {
                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                    } else {
                        ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
                    }
                }
                lignes.add(ligne);
                montantTotal += plat.getPrix() * qty;"""

replace_plat = """                    if (Boolean.TRUE.equals(isEmporter)) {
                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                    } else {
                        ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
                    }
                    lignes.add(ligne);
                }
                montantTotal += plat.getPrix() * qty;"""

content = content.replace(search_plat, replace_plat)

search_menu = """                    if (Boolean.TRUE.equals(isEmporter)) {
                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                    } else {
                        ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
                    }
                }
                lignes.add(ligne);
                montantTotal += menu.getPrix() * qty;"""

replace_menu = """                    if (Boolean.TRUE.equals(isEmporter)) {
                        ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                    } else {
                        ligne.setEtat(EtatLigneCommande.EN_VALIDATION);
                    }
                    lignes.add(ligne);
                }
                montantTotal += menu.getPrix() * qty;"""

content = content.replace(search_menu, replace_menu)

with open(file_path, "w") as f:
    f.write(content)
