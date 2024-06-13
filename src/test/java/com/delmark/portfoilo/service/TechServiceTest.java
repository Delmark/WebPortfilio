package com.delmark.portfoilo.service;

import com.delmark.portfoilo.exceptions.response.NoSuchTechException;
import com.delmark.portfoilo.exceptions.response.TechAlreadyExistsException;
import com.delmark.portfoilo.models.portfolio.Techs;
import com.delmark.portfoilo.repository.TechRepository;
import com.delmark.portfoilo.service.implementations.TechServiceImpl;
import com.delmark.portfoilo.service.interfaces.TechService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

public class TechServiceTest {

    private static final TechRepository techRepository = Mockito.mock(TechRepository.class);

    private static final TechService techService = new TechServiceImpl(techRepository);

    @Test
    void getAllTechs() {
        PageImpl<Techs> page = new PageImpl<>(List.of(new Techs(1L, "Java", "Java Desc"), new Techs(2L, "C++", "Pointers lol")));

        Mockito.when(techRepository.findAll(PageRequest.of(1, 10))).thenReturn(page);

        assertEquals(page, techService.getAllTechs(1));
    }

    @Test
    void getTechById() {
        Techs existingTech = new Techs(1L, "Java", "OOP");

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.of(existingTech));

        assertEquals(existingTech, techService.getTechsById(1L));
    }

    @Test
    void getTechByNonExistingId() {
        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NoSuchTechException.class, () -> techService.getTechsById(1L));
    }

    @Test
    void createTech() {
        Techs techToCreate = new Techs(null, "Java", "OOP Language");
        Techs expectedTech = new Techs(1L, "Java", "OOP Language");

        Mockito.when(techRepository.findByTechName("Java")).thenReturn(Optional.empty());
        Mockito.when(techRepository.save(techToCreate)).thenReturn(expectedTech);

        assertEquals(expectedTech, techService.createTech(techToCreate));
    }

    @Test
    void createTechWhenItAlreadyExists() {
        Techs techToCreate = new Techs(null, "Java", "OOP Language");
        Techs existingTech = new Techs(1L, "Java", "OOP Language");

        Mockito.when(techRepository.findByTechName("Java")).thenReturn(Optional.of(existingTech));

        assertThrows(TechAlreadyExistsException.class, () -> techService.createTech(techToCreate));
    }

    @Test
    void editTech() {
        Techs techData = new Techs(null, "Java", null);
        Techs existingTech = new Techs(1L, "Ja", "OOP Language");
        Techs expectedToSave = new Techs(1L, "Java", "OOP Language");
        Techs expectedTech = new Techs(1L, "Java", "OOP Language");

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.of(existingTech));
        Mockito.when(techRepository.save(expectedToSave)).thenReturn(expectedTech);

        assertEquals(expectedTech, techService.editTech(1L, techData));
    }

    @Test
    void editTechWithIdInject() {
        Techs techData = new Techs(3L, "Java", null);
        Techs existingTech = new Techs(1L, "Ja", "OOP Language");
        Techs expectedToSave = new Techs(1L, "Java", "OOP Language");
        Techs expectedTech = new Techs(1L, "Java", "OOP Language");

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.of(existingTech));
        Mockito.when(techRepository.save(expectedToSave)).thenReturn(expectedTech);

        assertEquals(expectedTech, techService.editTech(1L, techData));
    }


    @Test
    void editNonExistingTech() {
        Techs techData = new Techs(null, "Java", "OOP Language");

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchTechException.class, () -> techService.editTech(1L, techData));
    }

    @Test
    void deleteTech() {
        Techs existingTech = new Techs(1L, "Rust", null);

        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.of(existingTech));
        techService.deleteTech(1L);
        Mockito.verify(techRepository, Mockito.times(1)).delete(existingTech);
    }

    @Test
    void deleteNonExistingTech() {
        Mockito.when(techRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchTechException.class, () -> techService.deleteTech(1L));
    }

}
