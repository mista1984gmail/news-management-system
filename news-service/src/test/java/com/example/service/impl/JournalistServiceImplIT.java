package com.example.service.impl;

import com.example.entity.Journalist;
import com.example.entity.dto.AuthorDto;
import com.example.entity.dto.JournalistDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.JournalistMapper;
import com.example.repository.JournalistRepository;
import com.example.util.JournalistTestData;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true",
        "spring.mvc.pathmatch.matching-strategy = ANT_PATH_MATCHER"})
public class JournalistServiceImplIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:12")
            .withUsername("username")
            .withPassword("password")
            .withExposedPorts(5432)
            .withReuse(true);

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Autowired
    private JournalistRepository journalistRepository;

    @Autowired
    private JournalistMapper journalistMapper;

    @Autowired
    private JournalistServiceImpl journalistService;

    @Test
    void shouldCreateJournalist() {
        //given
        JournalistDto journalistForSave = JournalistTestData.builder()
                .withUsername("valda1")
                .withEmail("valda1@gmail.com")
                .withTelephone("+3757654321")
                .build()
                .buildJournalistDto();

        //when
        JournalistDto journalistSaved = journalistService.save(journalistForSave);

        //then
        assertEquals(journalistSaved.getUsername(), journalistForSave.getUsername());
        assertEquals(journalistSaved.getAddress(), journalistForSave.getAddress());
        assertEquals(journalistSaved.getEmail(), journalistForSave.getEmail());
        assertEquals(journalistSaved.getTelephone(), journalistForSave.getTelephone());
    }

    @Test
    void shouldFindJournalistById() {
        //given
        JournalistDto journalistForSave = JournalistTestData.builder()
                .withUsername("valda2")
                .withEmail("valda2@gmail.com")
                .withTelephone("+3757654322")
                .build()
                .buildJournalistDto();

        //when
        JournalistDto journalistSaved = journalistService.save(journalistForSave);
        JournalistDto journalistFromDBById = journalistService.findById(journalistSaved.getId());

        //then
        assertEquals(journalistFromDBById.getUsername(), journalistForSave.getUsername());
        assertEquals(journalistFromDBById.getAddress(), journalistForSave.getAddress());
        assertEquals(journalistFromDBById.getEmail(), journalistForSave.getEmail());
        assertEquals(journalistFromDBById.getTelephone(), journalistForSave.getTelephone());
    }

    @Test
    void shouldNotGetJournalistByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "Journalist with id: " + id + " not found";

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            journalistService.findById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotDeleteByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "Journalist with id: " + id + " not found";

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            journalistService.deleteById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldDeleteById() {
        //given
        JournalistDto journalistForSave = JournalistTestData.builder()
                .withUsername("valda3")
                .withEmail("valda3@gmail.com")
                .withTelephone("+3757654323")
                .build()
                .buildJournalistDto();

        //when
        JournalistDto journalistSaved = journalistService.save(journalistForSave);
        Long idFromDB = journalistSaved.getId();
        String errorMessage = "Journalist with id: " + idFromDB + " not found";
        journalistService.deleteById(idFromDB);
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            journalistService.findById(idFromDB);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotUpdateByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "Journalist with id: " + id + " not found";
        JournalistDto journalistForUpdate = JournalistTestData.builder()
                .withId(id)
                .withEmail("valda4@gmail.com")
                .withTelephone("+3757654324")
                .build()
                .buildJournalistDto();

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            journalistService.update(journalistForUpdate);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldFindFiveJournalists() {
        //give

        //when
        Page<JournalistDto> authors = journalistService.findAll(0, 5, "", "");

        //then
        assertEquals(5, authors.stream().toList().size());
    }

    @Test
    void shouldUpdateById() {
        //given
        JournalistDto journalistForSave = JournalistTestData.builder()
                .withUsername("valda5")
                .withEmail("valda5@gmail.com")
                .withTelephone("+3757654325")
                .build()
                .buildJournalistDto();

        JournalistDto journalistForUpdate = JournalistTestData.builder()
                .withUsername("valda5")
                .withEmail("valda5@gmail.com")
                .withTelephone("+3757654325")
                .withAddress("Grodno")
                .build()
                .buildJournalistDto();

        //when
        JournalistDto journalistSaved = journalistService.save(journalistForSave);
        Long id = journalistSaved.getId();
        journalistForUpdate.setId(id);
        journalistService.update(journalistForUpdate);
        Journalist actual = journalistRepository.findById(id).get();

        //then
        assertEquals(actual.getAddress(), journalistForUpdate.getAddress());
        assertEquals(actual.getUsername(), journalistForUpdate.getUsername());
        assertEquals(actual.getEmail(), journalistForUpdate.getEmail());
        assertEquals(actual.getTelephone(), journalistForUpdate.getTelephone());
        assertEquals(actual.getId(), journalistForUpdate.getId());
    }

}
