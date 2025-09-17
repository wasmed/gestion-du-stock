package com.example.demo.controller;

import com.example.demo.model.Feedback;
import com.example.demo.model.User;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserService userService;

    // Show the feedback form for clients
    @GetMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    public String showFeedbackForm(Model model) {
        model.addAttribute("feedback", new Feedback());
        return "feedback/create";
    }

    // Process the feedback submission
    @PostMapping("/create")
    @PreAuthorize("hasRole('CLIENT')")
    public String submitFeedback(@ModelAttribute Feedback feedback, Principal principal) {
        User client = userService.findUserByEmail(principal.getName());
        feedback.setClient(client);

        // Ajoute la date et l'heure actuelles
        feedback.setDateSubmitted(LocalDateTime.now());

        feedbackService.saveFeedback(feedback);
        return "redirect:/client/menu";
    }

    // Show the list of feedbacks for admins
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listFeedbacks(Model model) {
        List<Feedback> feedbacks = feedbackService.findAllFeedbacks();
        model.addAttribute("feedbacks", feedbacks);
        return "feedback/list";
    }
}
