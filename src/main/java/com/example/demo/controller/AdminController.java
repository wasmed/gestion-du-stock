package com.example.demo.controller;

import com.example.demo.model.Feedback;
import com.example.demo.model.StockProduit;
import com.example.demo.model.User;
import com.example.demo.service.FeedbackService;
import com.example.demo.service.StockService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {


    @Autowired
    private UserService userService;

    @Autowired
    private StockService stockService;

    @Autowired
    private FeedbackService feedbackService;

    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        // Gérer le tableau de bord avec toutes les données
        List<User> users = userService.findAllUsers();
        List<StockProduit> stocks = stockService.findAllStocks();
        List<Feedback> feedbacks = feedbackService.findAllFeedbacks();

        model.addAttribute("users", users);
        model.addAttribute("stocks", stocks);
        model.addAttribute("feedbacks", feedbacks);

        return "admin/dashboard";
    }
}
