package com.example.demo.service;

import com.example.demo.model.Plat;
import com.example.demo.repository.PlatRepository;
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
public class PlatServiceTest {

    @Mock
    private PlatRepository platRepository;

    @InjectMocks
    private PlatService platService;

    @Test
    void testFindAllPlats() {
        // Arrange
        Plat p1 = new Plat();
        p1.setNom("Pizza");
        Plat p2 = new Plat();
        p2.setNom("Pasta");
        List<Plat> expectedPlats = Arrays.asList(p1, p2);

        when(platRepository.findAll()).thenReturn(expectedPlats);

        // Act
        List<Plat> actualPlats = platService.findAllPlats();

        // Assert
        assertEquals(2, actualPlats.size());
        assertEquals(expectedPlats, actualPlats);
        verify(platRepository).findAll();
    }

    @Test
    void testSavePlat() {
        // Arrange
        Plat plat = new Plat();
        plat.setNom("Burger");

        when(platRepository.save(plat)).thenReturn(plat);

        // Act
        Plat savedPlat = platService.savePlat(plat);

        // Assert
        assertNotNull(savedPlat);
        assertEquals("Burger", savedPlat.getNom());
        verify(platRepository).save(plat);
    }

    @Test
    void testFindPlatById() {
        // Arrange
        Long id = 1L;
        Plat plat = new Plat();
        plat.setIdentifiant(id);
        plat.setNom("Salade");

        when(platRepository.findById(id)).thenReturn(Optional.of(plat));

        // Act
        Plat foundPlat = platService.findPlatById(id);

        // Assert
        assertNotNull(foundPlat);
        assertEquals(id, foundPlat.getIdentifiant());
        assertEquals("Salade", foundPlat.getNom());
        verify(platRepository).findById(id);
    }

    @Test
    void testDeletePlatById() {
        // Arrange
        Long id = 1L;

        // Act
        platService.deletePlatById(id);

        // Assert
        verify(platRepository).deleteById(id);
    }
}
