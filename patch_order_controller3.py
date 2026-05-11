import sys

file_path = "src/main/java/com/example/demo/controller/OrderManagementController.java"
with open(file_path, "r") as f:
    content = f.read()

search_plat = """                            if (existingLigne.isPresent()) {
                                ligne = existingLigne.get();
                                ligne.setQuantite(ligne.getQuantite() + quantite);
                            } else {
                                ligne = new LigneCommande();
                                ligne.setCommande(commande);
                                ligne.setPlat(plat);
                                ligne.setQuantite(quantite);
                                ligne.setTypeLigne(TypeLigneCommande.PLAT);
                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                                commande.getLignesCommande().add(ligne);
                            }
                            montantTotal += plat.getPrix() * quantite;
                            ligne = ligneCommandeRepository.save(ligne);
                            stockService.processStockDecrementForLigne(ligne);"""

replace_plat = """                            if (existingLigne.isPresent()) {
                                ligne = existingLigne.get();
                                ligne.setQuantite(ligne.getQuantite() + quantite);
                            } else {
                                ligne = new LigneCommande();
                                ligne.setCommande(commande);
                                ligne.setPlat(plat);
                                ligne.setQuantite(quantite);
                                ligne.setTypeLigne(TypeLigneCommande.PLAT);
                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                                commande.getLignesCommande().add(ligne);
                            }
                            montantTotal += plat.getPrix() * quantite;
                            ligne = ligneCommandeRepository.save(ligne);

                            LigneCommande deltaLigne = new LigneCommande();
                            deltaLigne.setPlat(plat);
                            deltaLigne.setQuantite(quantite);
                            stockService.processStockDecrementForLigne(deltaLigne);"""

content = content.replace(search_plat, replace_plat)

search_menu = """                            if (existingLigne.isPresent()) {
                                ligne = existingLigne.get();
                                ligne.setQuantite(ligne.getQuantite() + quantite);
                            } else {
                                ligne = new LigneCommande();
                                ligne.setCommande(commande);
                                ligne.setMenu(menu);
                                ligne.setQuantite(quantite);
                                ligne.setTypeLigne(TypeLigneCommande.MENU);
                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                                commande.getLignesCommande().add(ligne);
                            }
                            montantTotal += menu.getPrix() * quantite;
                            ligne = ligneCommandeRepository.save(ligne);
                            stockService.processStockDecrementForLigne(ligne);"""

replace_menu = """                            if (existingLigne.isPresent()) {
                                ligne = existingLigne.get();
                                ligne.setQuantite(ligne.getQuantite() + quantite);
                            } else {
                                ligne = new LigneCommande();
                                ligne.setCommande(commande);
                                ligne.setMenu(menu);
                                ligne.setQuantite(quantite);
                                ligne.setTypeLigne(TypeLigneCommande.MENU);
                                ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                                commande.getLignesCommande().add(ligne);
                            }
                            montantTotal += menu.getPrix() * quantite;
                            ligne = ligneCommandeRepository.save(ligne);

                            LigneCommande deltaLigne = new LigneCommande();
                            deltaLigne.setMenu(menu);
                            deltaLigne.setQuantite(quantite);
                            stockService.processStockDecrementForLigne(deltaLigne);"""

content = content.replace(search_menu, replace_menu)

with open(file_path, "w") as f:
    f.write(content)
