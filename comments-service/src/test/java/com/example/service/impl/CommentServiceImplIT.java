package com.example.service.impl;

import com.example.controller.request.CommentRequest;
import com.example.entity.Comment;
import com.example.entity.dto.CommentDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.CommentMapper;
import com.example.newsservice.NewsServiceClient;
import com.example.repository.CommentRepository;
import com.example.service.AuthorService;
import com.example.util.CommentTestData;
import com.example.util.ConstantsForTest;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@WireMockTest(httpPort = 8081)
@SpringBootTest(properties = {"spring.main.allow-bean-definition-overriding=true",
        "spring.mvc.pathmatch.matching-strategy = ANT_PATH_MATCHER"})
public class CommentServiceImplIT {

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

    private static final String BASE_URL = "http://localhost:8081";
    private static final String NEWS_URL = "/api/v1/news/";

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private NewsServiceClient newsServiceClient;

    @Autowired
    private CommentServiceImpl commentService;

    @Test
    void shouldCreateComment() {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        CommentRequest commentRequest = CommentTestData.builder()
                .build()
                .buildCommentRequest();
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        CommentDto actual = commentService.save(newsId, commentRequest);

        //then
        assertEquals(actual.getText(), commentRequest.getText());
        assertEquals(actual.getAuthor().getUsername(), commentRequest.getUsername());
    }

    @Test
    void shouldFindCommentById() {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        CommentRequest commentRequest = CommentTestData.builder()
                .build()
                .buildCommentRequest();
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        CommentDto actual = commentService.save(newsId, commentRequest);
        CommentDto commentFromDBById = commentService.findById(newsId, actual.getId());

        //then
        assertEquals(commentFromDBById.getText(), commentRequest.getText());
        assertEquals(commentFromDBById.getAuthor().getUsername(), commentRequest.getUsername());
    }

    @Test
    void shouldNotGetCommentByIdAndThrowsEntityNotFoundException() {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long id = 100L;
        String errorMessage = "Comment with id: " + id + " not found";
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            commentService.findById(newsId, id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotDeleteByIdAndThrowsEntityNotFoundException() {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long id = 100L;
        String errorMessage = "Comment with id: " + id + " not found";
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteById(newsId, id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldDeleteById() {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        CommentRequest commentRequest = CommentTestData.builder()
                .build()
                .buildCommentRequest();
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        CommentDto actual = commentService.save(newsId, commentRequest);
        Long idFromDB = actual.getId();
        String errorMessage = "Comment with id: " + idFromDB + " not found";
        commentService.deleteById(newsId, idFromDB);
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            commentService.findById(newsId, idFromDB);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldNotUpdateByIdAndThrowsEntityNotFoundException() {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long id = 100L;
        String errorMessage = "Comment with id: " + id + " not found";
        CommentRequest commentRequest = CommentTestData.builder()
                .build()
                .buildCommentRequest();
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            commentService.update(newsId, id, commentRequest);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldFindFiveComment() {
        //give
        Long newsId = ConstantsForTest.NEWS_ID;
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        Page<CommentDto> comments = commentService.findAllCommentsByNewsId(newsId, 0, 5, "", "");

        //then
        assertEquals(5, comments.stream().toList().size());
    }

    @Test
    void shouldUpdateById() {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        CommentRequest commentRequest = CommentTestData.builder()
                .build()
                .buildCommentRequest();
        CommentRequest commentForUpdate = CommentTestData.builder()
                .withText("New comment.")
                .build()
                .buildCommentRequest();
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        //when
        CommentDto commentSaved = commentService.save(newsId, commentRequest);
        Long id = commentSaved.getId();
        commentService.update(newsId, id, commentForUpdate);
        Comment actual = commentRepository.findById(id).get();

        //then
        assertEquals(commentForUpdate.getText(), actual.getText());
    }


}
