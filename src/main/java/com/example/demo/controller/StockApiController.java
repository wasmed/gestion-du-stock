package com.example.demo.controller;

import com.example.demo.model.StockProduit;
import com.example.demo.service.StockAiService;
import com.example.demo.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@PreAuthorize("hasAnyRole('ADMIN', 'CHEF_CUISINIER')")
public class StockApiController {

    @Autowired
    private StockAiService stockAiService;

    @Autowired
    private StockService stockService;

    @GetMapping("/alerte-ia")
    public String getAlerteIa() {
        return stockAiService.analyseStock();
    }

    @GetMapping("/etat-general")
    public List<StockProduit> getEtatGeneral() {
        return stockService.findAllStocks();
    }
}
