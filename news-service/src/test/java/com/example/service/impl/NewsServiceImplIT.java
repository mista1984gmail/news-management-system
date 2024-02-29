package com.example.service.impl;

import com.example.controller.request.NewsRequest;
import com.example.entity.News;
import com.example.entity.dto.NewsDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.JournalistMapper;
import com.example.mapper.NewsMapper;
import com.example.repository.NewsRepository;
import com.example.service.JournalistService;
import com.example.util.NewsTestData;
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
public class NewsServiceImplIT {

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
    private NewsRepository newsRepository;

    @Autowired
    private JournalistService journalistService;

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private JournalistMapper journalistMapper;

    @Autowired
    private NewsServiceImpl newsService;

    @Test
    void shouldCreateJournalist() {
        //given
        NewsDto newsForSave = NewsTestData.builder()
                .build()
                .buildNewsDto();
        NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();

        //when
        NewsDto newsSaved = newsService.save(newsRequest);

        //then
        assertEquals(newsSaved.getText(), newsForSave.getText());
        assertEquals(newsSaved.getTitle(), newsForSave.getTitle());
        assertEquals(newsSaved.getJournalist().getUsername(), newsForSave.getJournalist().getUsername());
    }

    @Test
    void shouldFindNewsById() {
        //given
        NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();

        //when
        NewsDto newsSaved = newsService.save(newsRequest);
        NewsDto newsFromDBById = newsService.findById(newsSaved.getId());

        //then
        assertEquals(newsFromDBById.getText(), newsRequest.getText());
        assertEquals(newsFromDBById.getTitle(), newsRequest.getTitle());
        assertEquals(newsFromDBById.getJournalist().getUsername(), newsRequest.getUsername());
    }

    @Test
    void shouldNotGetNewsByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "News with id: " + id + " not found";

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            newsService.findById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotDeleteByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        String errorMessage = "News with id: " + id + " not found";

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            newsService.deleteById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldDeleteById() {
        //given
        NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();
        NewsDto newsForSave = NewsTestData.builder()
                .build()
                .buildNewsDto();

        //when
        NewsDto newsSaved = newsService.save(newsRequest);
        Long idFromDB = newsSaved.getId();
        String errorMessage = "News with id: " + idFromDB + " not found";
        newsService.deleteById(idFromDB);
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            newsService.findById(idFromDB);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotUpdateByIdAndThrowsEntityNotFoundException() {
        //given
        Long id = 100L;
        NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();
        String errorMessage = "News with id: " + id + " not found";

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            newsService.update(id, newsRequest);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldFindFiveNews() {
        //give

        //when
        Page<NewsDto> news = newsService.findAllWithPaginationAndSorting(0, 5, "", "");

        //then
        assertEquals(5, news.stream().toList().size());
    }

     @Test
    void shouldUpdateById() {
        //given
         NewsRequest newsRequestForUpdate = NewsTestData.builder()
                 .withText("New text")
                 .build()
                 .buildNewsRequest();
         NewsDto newsForUpdate = NewsTestData.builder()
                .withText("New text")
                .build()
                .buildNewsDto();
         NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();

        //when
         NewsDto newsSaved = newsService.save(newsRequest);
         Long id = newsSaved.getId();
         newsForUpdate.setId(id);
        newsService.update(id, newsRequestForUpdate);
        News actual = newsRepository.findById(id).get();

        //then
        assertEquals(actual.getText(), newsForUpdate.getText());
        assertEquals(actual.getTitle(), newsForUpdate.getTitle());
    }

}
