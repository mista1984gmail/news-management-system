package com.example.service.impl;

import com.example.entity.Author;
import com.example.entity.dto.AuthorDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.AuthorMapper;
import com.example.repository.AuthorRepository;
import com.example.util.AuthorTestData;
import org.junit.jupiter.api.Test;
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
public class AuthorServiceImplIT {

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
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private AuthorServiceImpl authorService;

    @Test
    void shouldCreateAuthor() {
        //given
        AuthorDto authorForSave = AuthorTestData.builder()
                .withEmail("valda1@gmail.com")
                .withTelephone("+3757654321")
                .build()
                .buildAuthorDto();

        //when
        AuthorDto authorSaved = authorService.save(authorForSave);

        //then
        assertEquals(authorSaved.getUsername(), authorForSave.getUsername());
        assertEquals(authorSaved.getAddress(), authorForSave.getAddress());
        assertEquals(authorSaved.getEmail(), authorForSave.getEmail());
        assertEquals(authorSaved.getTelephone(), authorForSave.getTelephone());
    }

    @Test
    void shouldFindAuthorById() {
        //given
        AuthorDto authorForSave = AuthorTestData.builder()
                .withUsername("valda2")
                .withEmail("valda2@gmail.com")
                .withTelephone("+3757654322")
                .build()
                .buildAuthorDto();

        //when
        AuthorDto authorSaved = authorService.save(authorForSave);
        AuthorDto authorFromDBById = authorService.findById(authorSaved.getId());

        //then
        assertEquals(authorFromDBById.getUsername(), authorForSave.getUsername());
        assertEquals(authorFromDBById.getAddress(), authorForSave.getAddress());
        assertEquals(authorFromDBById.getEmail(), authorForSave.getEmail());
        assertEquals(authorFromDBById.getTelephone(), authorForSave.getTelephone());
    }

    @Test
    void shouldNotGetAuthorByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "Author with id: " + id + " not found";

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            authorService.findById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotDeleteByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "Author with id: " + id + " not found";

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            authorService.deleteById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldDeleteById() {
        //given
        AuthorDto authorForSave = AuthorTestData.builder()
                .withEmail("valda3@gmail.com")
                .withTelephone("+3757654323")
                .build()
                .buildAuthorDto();

        //when
        AuthorDto authorSaved = authorService.save(authorForSave);
        Long idFromDB = authorSaved.getId();
        String errorMessage = "Author with id: " + idFromDB + " not found";
        authorService.deleteById(idFromDB);
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            authorService.findById(idFromDB);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotUpdateByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "Author with id: " + id + " not found";
        AuthorDto authorForUpdate = AuthorTestData.builder()
                .withId(id)
                .withEmail("valda4@gmail.com")
                .withTelephone("+3757654324")
                .build()
                .buildAuthorDto();

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            authorService.update(authorForUpdate);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldFindFiveAuthors() {
        //give

        //when
        Page<AuthorDto> authors = authorService.findAll(0, 5, "", "");

        //then
        assertEquals(5, authors.stream().toList().size());
    }

    @Test
    void shouldUpdateById() {
        //given
        AuthorDto authorForSave = AuthorTestData.builder()
                .withUsername("valda5")
                .withEmail("valda5@gmail.com")
                .withTelephone("+3757654325")
                .build()
                .buildAuthorDto();

        AuthorDto authorForUpdate = AuthorTestData.builder()
                .withUsername("valda5")
                .withEmail("valda5@gmail.com")
                .withTelephone("+3757654325")
                .withAddress("Grodno")
                .build()
                .buildAuthorDto();

        //when
        AuthorDto authorSaved = authorService.save(authorForSave);
        Long id = authorSaved.getId();
        authorForUpdate.setId(id);
        authorService.update(authorForUpdate);
        Author actual = authorRepository.findById(id).get();

        //then
        assertEquals(actual.getAddress(), authorForUpdate.getAddress());
        assertEquals(actual.getUsername(), authorForUpdate.getUsername());
        assertEquals(actual.getEmail(), authorForUpdate.getEmail());
        assertEquals(actual.getTelephone(), authorForUpdate.getTelephone());
        assertEquals(actual.getId(), authorForUpdate.getId());
    }

}
