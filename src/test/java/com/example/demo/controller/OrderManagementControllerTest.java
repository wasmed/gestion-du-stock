package com.example.demo.controller;

import com.example.demo.repository.CommandeRepository;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommandeRepository commandeRepository;

    @Test
    @WithMockUser(username = "serveur@resto.com", roles = {"SERVEUR"})
    public void testListOrders() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("serveur/dashboard"))
                .andExpect(model().attributeExists("commandesParTable"))
                .andExpect(model().attributeExists("tablesOccupees"))
                .andExpect(model().attributeExists("commandesAValider"))
                .andExpect(model().attributeExists("commandesPretes"));
    }

    @Test
    @WithMockUser(username = "serveur@resto.com", roles = {"SERVEUR"})
    public void testCreateEmptyOrder_ShouldFail() throws Exception {
        long initialCount = commandeRepository.count();

        // Attempt to create an order with no plats and no menus
        mockMvc.perform(post("/orders/create")
                        .param("tableId", "1") // Assuming table with ID 1 exists
                        .param("clientEmail", "guest@resto.com")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/create"))
                .andExpect(flash().attribute("errorMessage", "Veuillez sélectionner au moins un plat ou un menu."));

        long newCount = commandeRepository.count();
        // The bug should be fixed, so the count should NOT increase.
        assertEquals(initialCount, newCount, "Fix verified: Empty order was NOT created");
    }
}
