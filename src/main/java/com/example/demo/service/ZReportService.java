package com.example.demo.service;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.model.ModePaiement;
import com.example.demo.repository.CommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ZReportService {

    @Autowired
    private CommandeRepository commandeRepository;

    public Map<ModePaiement, Double> generateZReport() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Commande> commandesAujourdhui = commandeRepository.findAll().stream()
                .filter(c -> c.getEtat() == EtatCommande.PAYEE)
                .filter(c -> c.getDateHeure() != null && !c.getDateHeure().isBefore(startOfDay) && !c.getDateHeure().isAfter(endOfDay))
                .collect(Collectors.toList());

        Map<ModePaiement, Double> report = new HashMap<>();
        for (ModePaiement mode : ModePaiement.values()) {
            report.put(mode, 0.0);
        }

        for (Commande commande : commandesAujourdhui) {
            if (commande.getPaiement() != null && commande.getPaiement().getModePaiement() != null) {
                ModePaiement mode = commande.getPaiement().getModePaiement();
                Double montant = commande.getPaiement().getMontant(); // Uses montant with pourboire or montantTotal
                report.put(mode, report.get(mode) + (montant != null ? montant : 0.0));
            }
        }

        return report;
    }
}
