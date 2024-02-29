package com.example.controller;

import com.example.controller.request.CommentRequest;
import com.example.controller.response.CommentResponse;
import com.example.entity.dto.CommentDto;
import com.example.mapper.CommentMapper;
import com.example.security.SecurityService;
import com.example.service.CommentService;
import com.example.util.CommentTestData;
import com.example.util.ConstantsForTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    private final String RESOURCE_URL = "/api/v1/news";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CommentMapper commentMapper;

    @MockBean
    private SecurityService securityService;

    @Test
    void shouldGetCommentById() throws Exception {
        //given
        Long commentId = ConstantsForTest.COMMENT_ID;
        Long newsId = ConstantsForTest.NEWS_ID;

        CommentDto comment = CommentTestData.builder().build().buildCommentDto();
        CommentResponse commentResponse = CommentTestData.builder().build().buildCommentResponse();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForAuthorOperationCRUD(any());
        when(commentService.findById(newsId, commentId)).thenReturn(comment);
        when(commentMapper.dtoToResponse(comment)).thenReturn(commentResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(RESOURCE_URL + "/" + newsId + "/comments/" + commentId)
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(commentResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updateDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("Orci nulla pellentesque dignissim enim sit amet venenatis urna cursus eget nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi tincidunt augue interdum velit euismod in."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value("Valda"));

        verify(commentService, times(1)).findById(newsId, commentId);
    }

    @Test
    void shouldSaveComment() throws Exception {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;

        CommentDto commentSaved = CommentTestData.builder().build().buildCommentDto();
        CommentResponse commentResponse = CommentTestData.builder().build().buildCommentResponse();
        CommentRequest commentRequest = new CommentRequest("Orci nulla pellentesque dignissim enim sit amet venenatis urna cursus eget nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi tincidunt augue interdum velit euismod in.", "valda");

        //when
        doNothing()
                .when(securityService)
                .checkAccessForAuthorOperationCRUD(any());
        when(commentService.save(newsId, commentRequest)).thenReturn(commentSaved);
        when(commentMapper.dtoToResponse(commentSaved)).thenReturn(commentResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(RESOURCE_URL + "/" + newsId + "/comments")
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(commentResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updateDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("Orci nulla pellentesque dignissim enim sit amet venenatis urna cursus eget nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi tincidunt augue interdum velit euismod in."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value("Valda"));

        verify(commentService, times(1)).save(newsId, commentRequest);
    }

    @Test
    void shouldFailSave() throws Exception {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;

        CommentDto commentSaved = CommentTestData.builder().build().buildCommentDto();
        CommentResponse commentResponse = CommentTestData.builder().build().buildCommentResponse();
        CommentRequest commentRequest = CommentTestData.builder().build().buildCommentRequest();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForAuthorOperationCRUD(any());

        when(commentService.save(newsId, commentRequest)).thenReturn(commentSaved);
        when(commentMapper.dtoToResponse(commentSaved)).thenReturn(commentResponse);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RESOURCE_URL + "/" + newsId + "/comments")
                                .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        verify(commentService, never()).save(newsId, commentRequest);
    }

}