package com.example.demo.service;

import com.example.demo.dto.ItemStatDTO;
import com.example.demo.repository.CommandeRepository;
import com.example.demo.repository.FeedbackRepository;
import com.example.demo.repository.LigneCommandeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatistiqueService {

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    public List<ItemStatDTO> getTopSellingPlats() {
        return ligneCommandeRepository.findTopSellingPlats();
    }

    public List<ItemStatDTO> getBottomSellingPlats() {
        return ligneCommandeRepository.findBottomSellingPlats();
    }

    public List<ItemStatDTO> getTopSellingMenus() {
        return ligneCommandeRepository.findTopSellingMenus();
    }

    public List<ItemStatDTO> getBottomSellingMenus() {
        return ligneCommandeRepository.findBottomSellingMenus();
    }

    public List<ItemStatDTO> getTopRatedPlats() {
        return feedbackRepository.findTopRatedPlats();
    }

    public List<ItemStatDTO> getBottomRatedPlats() {
        return feedbackRepository.findBottomRatedPlats();
    }

    public List<ItemStatDTO> getTopRatedMenus() {
        return feedbackRepository.findTopRatedMenus();
    }

    public List<ItemStatDTO> getBottomRatedMenus() {
        return feedbackRepository.findBottomRatedMenus();
    }

    public List<ItemStatDTO> getCommandesPerDay() {
        return commandeRepository.findCommandesPerDay();
    }
}
