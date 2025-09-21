package com.example.demo.service;

import com.example.demo.model.Commande;
import com.example.demo.model.Feedback;
import com.example.demo.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {
    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> findAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    public void createFeedback(Commande commande, int note, String commentaire) {
        // On vérifie à nouveau qu'il n'y a pas déjà un feedback pour être sûr
        if (commande.getFeedBack() != null) {
            throw new IllegalStateException("Un feedback existe déjà pour cette commande.");
        }

        Feedback feedback = new Feedback();
        feedback.setCommande(commande);
        feedback.setNote(note);
        feedback.setCommentaire(commentaire);
        feedback.setDateSubmitted(LocalDateTime.now());
        feedbackRepository.save(feedback);
    }
}
