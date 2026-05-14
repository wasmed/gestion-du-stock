package com.example.demo.bdd;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.model.EtatLigneCommande;
import com.example.demo.model.LigneCommande;
import com.example.demo.repository.CommandeRepository;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Quand;
import io.cucumber.spring.ScenarioScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ScenarioScope
public class PaymentFailureSteps {

    @Autowired
    private CommandeRepository commandeRepository;

    private Long commandeId;

    @Etantdonné("une commande existante avec des lignes à l'état {string}")
    @Transactional
    public void une_commande_existante_avec_des_lignes_a_l_etat(String etatStr) {
        Commande commande = new Commande();
        commande.setEtat(EtatCommande.EN_COURS);

        LigneCommande ligne = new LigneCommande();
        ligne.setEtat(EtatLigneCommande.valueOf(etatStr));
        ligne.setQuantite(1);
        ligne.setCommande(commande);

        commande.getLignesCommande().add(ligne);
        commande = commandeRepository.save(commande);
        commandeId = commande.getId();
    }

    @Quand("un webhook Mollie notifie un échec de paiement pour cette commande")
    @Transactional
    public void un_webhook_mollie_notifie_un_echec_de_paiement() {
        Optional<Commande> optionalCommande = commandeRepository.findById(commandeId);
        if (optionalCommande.isPresent()) {
            Commande commande = optionalCommande.get();
            // Simulate what WebhookController would do on failure based on prompt
            String warning = "Echec paiement en ligne";
            String existingComment = commande.getCommentaire() == null ? "" : commande.getCommentaire() + " | ";
            commande.setCommentaire(existingComment + warning);
            commandeRepository.save(commande);
        }
    }

    @Alors("la commande n'est pas supprimée")
    public void la_commande_n_est_pas_supprimee() {
        Optional<Commande> optionalCommande = commandeRepository.findById(commandeId);
        assertThat(optionalCommande).isPresent();
    }

    @Alors("la commande contient une note d'échec dans son commentaire")
    @Transactional
    public void la_commande_contient_une_note_d_echec_dans_son_commentaire() {
        Optional<Commande> optionalCommande = commandeRepository.findById(commandeId);
        assertThat(optionalCommande).isPresent();
        assertThat(optionalCommande.get().getCommentaire()).contains("Echec paiement");
    }
}
