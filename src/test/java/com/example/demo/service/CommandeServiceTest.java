package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommandeServiceTest {

    @Mock
    private CommandeRepository commandeRepository;

    @Mock
    private TableRestaurantRepository tableRepository;

    @Mock
    private PlatRepository platRepository;

    @Mock
    private LigneCommandeRepository ligneCommandeRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private ConsommationStockRepository consommationStockRepository;

    @InjectMocks
    private CommandeService commandeService;

    @Test
    void testCreateNewCommandeWithTable() {
        // Arrange
        User client = new User();
        client.setId(1L);
        User serveur = new User();
        serveur.setId(2L);
        Long tableId = 1L;
        TableRestaurant table = new TableRestaurant();
        table.setIdentifiant(tableId);

        when(tableRepository.findById(tableId)).thenReturn(Optional.of(table));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Commande createdCommande = commandeService.createNewCommande(client, serveur, tableId);

        // Assert
        assertNotNull(createdCommande);
        assertEquals(client, createdCommande.getClient());
        assertEquals(serveur, createdCommande.getServeur());
        assertEquals(table, createdCommande.getTable());
        assertEquals(EtatCommande.EN_ATTENTE, createdCommande.getEtat());
        verify(tableRepository).findById(tableId);
        verify(commandeRepository).save(any(Commande.class));
    }

    @Test
    void testUpdateCommandeEtat() {
        // Arrange
        Long commandeId = 1L;
        Commande commande = new Commande();
        commande.setId(commandeId);
        commande.setEtat(EtatCommande.EN_ATTENTE);

        when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(Commande.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Commande updatedCommande = commandeService.updateCommandeEtat(commandeId, EtatCommande.EN_PREPARATION);

        // Assert
        assertNotNull(updatedCommande);
        assertEquals(EtatCommande.EN_PREPARATION, updatedCommande.getEtat());
        verify(commandeRepository).findById(commandeId);
        verify(commandeRepository).save(commande);
    }

    @Test
    void testAddPlatToCommande() {
        // Arrange
        Commande commande = new Commande();
        commande.setId(1L);
        Long platId = 10L;
        Plat plat = new Plat();
        plat.setIdentifiant(platId);

        when(platRepository.findById(platId)).thenReturn(Optional.of(plat));
        when(ligneCommandeRepository.save(any(LigneCommande.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Commande resultCommande = commandeService.addPlatToCommande(commande, platId, 2);

        // Assert
        assertNotNull(resultCommande);
        verify(platRepository).findById(platId);
        verify(ligneCommandeRepository).save(any(LigneCommande.class));
    }

    @Test
    void testGetCommandesEnAttente() {
        // Arrange
        Commande c1 = new Commande();
        c1.setEtat(EtatCommande.EN_ATTENTE);
        Commande c2 = new Commande();
        c2.setEtat(EtatCommande.EN_ATTENTE);
        List<Commande> expectedList = Arrays.asList(c1, c2);

        when(commandeRepository.findByEtat(EtatCommande.EN_ATTENTE)).thenReturn(expectedList);

        // Act
        List<Commande> actualList = commandeService.getCommandesEnAttente();

        // Assert
        assertEquals(2, actualList.size());
        assertEquals(expectedList, actualList);
        verify(commandeRepository).findByEtat(EtatCommande.EN_ATTENTE);
    }
}
