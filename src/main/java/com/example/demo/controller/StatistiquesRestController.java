package com.example.demo.controller;

import com.example.demo.service.StatistiqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("hasRole('ADMIN')")
public class StatistiquesRestController {

    @Autowired
    private StatistiqueService statistiqueService;

    @GetMapping("/analyse-ia")
    public String analyseStats() {
        return statistiqueService.analyseStatistiques();
    }
}
