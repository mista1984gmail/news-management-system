package com.example.service.impl;

import com.example.controller.request.CommentRequest;
import com.example.entity.Author;
import com.example.entity.Comment;
import com.example.entity.dto.CommentDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.CommentMapper;
import com.example.newsservice.NewsServiceClient;
import com.example.repository.CommentRepository;
import com.example.service.AuthorService;
import com.example.util.AuthorTestData;
import com.example.util.CommentTestData;
import com.example.util.ConstantsForTest;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@WireMockTest(httpPort = 8081)
class CommentServiceImplTest {

    private static final String BASE_URL = "http://localhost:8081";
    private static final String NEWS_URL = "/api/v1/news/";

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private AuthorService authorService;

    @Mock
    private NewsServiceClient newsServiceClient;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void shouldCreateComment() {
        // given
        Long newsId = ConstantsForTest.NEWS_ID;
        Comment comment = CommentTestData.builder().build().buildComment();
        CommentDto expected = CommentTestData.builder().build().buildCommentDto();
        CommentDto commentDtoSaved = CommentTestData.builder().build().buildCommentDto();
        CommentRequest commentRequest = CommentTestData.builder().build().buildCommentRequest();
        Author author = AuthorTestData.builder()
                .build()
                .buildAuthor();

        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        when(authorService.findByUsername(commentRequest.getUsername()))
                .thenReturn(author);
        when(commentRepository.save(any()))
                .thenReturn(comment);
        when(commentMapper.entityToDto(comment))
                .thenReturn(commentDtoSaved);

        //when
        CommentDto actual = commentService.save(newsId, commentRequest);

        //then
        verify(commentRepository).save(any());
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteComment(){
        // given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long commentId = ConstantsForTest.COMMENT_ID;
        Optional <Comment> comment = Optional.of(CommentTestData.builder().build().buildComment());
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        when(commentRepository.findById(commentId))
                .thenReturn(comment);
        doNothing()
                .when(commentRepository)
                .delete(comment.get());

        //when
        commentService.deleteById(newsId, commentId);

        //then
        verify(commentRepository).delete(any());
    }

    @Test
    void shouldNotDeleteCommentAndThrowsCommentNotFoundException() {
        // given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long commentId = ConstantsForTest.COMMENT_ID;
        String errorMessage = "Comment with id: " + commentId + " not found";
        Optional <Comment> comment = Optional.of(CommentTestData.builder().build().buildComment());
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        when(commentRepository.findById(commentId))
                .thenThrow(new EntityNotFoundException(Comment.class, commentId));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            commentService.deleteById(newsId, commentId);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
        verify(commentRepository, never()).delete(comment.get());
    }

    @Test
    void shouldGetCommentById() {
        // given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long commentId = ConstantsForTest.COMMENT_ID;
        Optional <Comment> comment = Optional.of(CommentTestData.builder().build().buildComment());
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));
        CommentDto expected = CommentTestData.builder().build().buildCommentDto();

        when(commentRepository.findById(commentId))
                .thenReturn(comment);
        when(commentMapper.entityToDto(comment.get()))
                .thenReturn(expected);

        //when
        CommentDto actual = commentService.findById(newsId, commentId);

        //then
        assertEquals(expected, actual);
        verify(commentRepository).findById(commentId);
        verify(commentMapper).entityToDto(comment.get());
    }

    @Test
    void shouldNotGetCommentByIdAndThrowsCommentNotFoundException() {
        // given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long commentId = ConstantsForTest.COMMENT_ID;
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));
        String errorMessage = "Comment with id: " + commentId + " not found";

        when(commentRepository.findById(commentId))
                .thenThrow(new EntityNotFoundException(Comment.class, commentId));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            commentService.findById(newsId, commentId);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldUpdateComment() {
        // given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long commentId = ConstantsForTest.COMMENT_ID;
        CommentDto commentDtoSaved = CommentTestData.builder()
                .withText("Ac ut consequat semper viverra nam libero justo laoreet sit amet cursus sit amet dictum sit amet.")
                .build()
                .buildCommentDto();
        Optional <Comment> comment = Optional.of(CommentTestData.builder().build().buildComment());
        CommentRequest commentRequestForUpdate = CommentTestData.builder()
                .withText("Ac ut consequat semper viverra nam libero justo laoreet sit amet cursus sit amet dictum sit amet.")
                .build()
                .buildCommentRequest();
        Comment commentFromDB = CommentTestData.builder()
                .withText("Ac ut consequat semper viverra nam libero justo laoreet sit amet cursus sit amet dictum sit amet.")
                .build()
                .buildComment();

        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));

        Author author = AuthorTestData.builder()
                .build()
                .buildAuthor();

        when(commentRepository.findById(commentId))
                .thenReturn(comment);
        when(authorService.findByUsername(commentRequestForUpdate.getUsername()))
                .thenReturn(author);
        when(commentRepository.save(any()))
                .thenReturn(commentFromDB);
        when(commentMapper.entityToDto(commentFromDB))
                .thenReturn(commentDtoSaved);

        //when
        CommentDto expected = commentService.update(newsId, commentId, commentRequestForUpdate);

        //then
        verify(commentRepository).save(any());
        assertThat(commentRequestForUpdate)
                .hasFieldOrPropertyWithValue(Comment.Fields.text, expected.getText());
    }

    @Test
    void shouldNotUpdateCommentAndThrowsHouseNotFoundException() {
        // given
        Long newsId = ConstantsForTest.NEWS_ID;
        Long commentId = ConstantsForTest.COMMENT_ID;
        stubFor(get(urlEqualTo(NEWS_URL + newsId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("news.json")));
        String errorMessage = "Comment with id: " + commentId + " not found";
        CommentRequest commentRequestForUpdate = CommentTestData.builder()
                .withText("Ac ut consequat semper viverra nam libero justo laoreet sit amet cursus sit amet dictum sit amet.")
                .build()
                .buildCommentRequest();

        when(commentRepository.findById(commentId))
                .thenThrow(new EntityNotFoundException(Comment.class, commentId));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            commentService.update(newsId, commentId, commentRequestForUpdate);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }


}