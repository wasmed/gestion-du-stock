package com.example.demo.controller;

import com.example.demo.model.Commande;
import com.example.demo.model.EtatCommande;
import com.example.demo.model.TableRestaurant;
import com.example.demo.repository.LigneCommandeRepository;
import com.example.demo.repository.TableRestaurantRepository;
import com.example.demo.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderManagementController.class)
public class OrderManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MenuService menuService;

    @MockBean
    private LigneCommandeRepository ligneCommandeRepository;

    @MockBean
    private CommandeService commandeService;

    @MockBean
    private PlatService platService;

    @MockBean
    private TableRestaurantRepository tableRepository;

    @MockBean
    private StockService stockService;

    @Test
    @WithMockUser(roles = "SERVEUR")
    public void testListOrders() throws Exception {
        // Arrange
        List<TableRestaurant> tables = new ArrayList<>();
        TableRestaurant t1 = new TableRestaurant();
        t1.setIdentifiant(1L);
        t1.setNumeroTable(1);
        tables.add(t1);

        when(tableRepository.findAll()).thenReturn(tables);

        List<Commande> commandesEnCours = new ArrayList<>();
        Commande c1 = new Commande();
        c1.setId(100L);
        c1.setEtat(EtatCommande.EN_PREPARATION);
        c1.setTable(t1);

        com.example.demo.model.User client = new com.example.demo.model.User();
        client.setFullName("Jean Dupont");
        c1.setClient(client);

        commandesEnCours.add(c1);

        when(commandeService.getCommandesEnCoursEtServies()).thenReturn(commandesEnCours);

        List<Commande> commandesAValider = new ArrayList<>();
        when(commandeService.getCommandesAValider()).thenReturn(commandesAValider);

        // Act & Assert
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("serveur/dashboard"))
                .andExpect(model().attributeExists("toutesLesTables"))
                .andExpect(model().attributeExists("commandesParTable"))
                .andExpect(model().attributeExists("tablesOccupees"))
                .andExpect(model().attributeExists("commandesAValider"))
                .andExpect(model().attribute("toutesLesTables", hasSize(1)));
    }
}
