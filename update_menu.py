import sys

file_path = "src/main/java/com/example/demo/controller/OrderManagementController.java"
with open(file_path, "r") as f:
    content = f.read()

search = """                            LigneCommande ligne = new LigneCommande();
                            ligne.setCommande(commande);
                            ligne.setMenu(menu);
                            ligne.setQuantite(quantite);
                            ligne.setTypeLigne(TypeLigneCommande.MENU);
                            ligne.setEtat(EtatLigneCommande.EN_ATTENTE);
                            montantTotal += menu.getPrix() * quantite;
                            ligne = ligneCommandeRepository.save(ligne);
                            stockService.processStockDecrementForLigne(ligne);
                            commande.getLignesCommande().add(ligne); // <-- LA LIGNE CLÉ À AJOUTER"""

replace = """                            Optional<LigneCommande> existingLigne = commande.getLignesCommande().stream()
                                    .filter(l -> l.getMenu() != null && l.getMenu().getId().equals(menu.getId()) && l.getEtat() == EtatLigneCommande.EN_ATTENTE)
                                    .findFirst();

                            LigneCommande ligne;
                            if (existingLigne.isPresent()) {
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

content = content.replace(search, replace)

with open(file_path, "w") as f:
    f.write(content)
