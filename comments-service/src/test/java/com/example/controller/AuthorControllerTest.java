package com.example.controller;

import com.example.controller.request.AuthorRequest;
import com.example.controller.response.AuthorResponse;
import com.example.entity.Author;
import com.example.entity.dto.AuthorDto;
import com.example.mapper.AuthorMapper;
import com.example.security.SecurityService;
import com.example.service.AuthorService;
import com.example.util.AuthorTestData;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    private final String RESOURCE_URL = "/api/v1/authors";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private AuthorMapper authorMapper;

    @MockBean
    private SecurityService securityService;

    @Test
    void shouldGetAuthorsById() throws Exception {
        //given
        Long authorId = ConstantsForTest.AUTHOR_ID;
        AuthorDto author = AuthorTestData.builder().build().buildAuthorDto();
        AuthorResponse authorResponse = AuthorTestData.builder().build().buildAuthorResponse();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForAuthorOperationCRUD(any());
        when(authorService.findById(authorId)).thenReturn(author);
        when(authorMapper.dtoToResponse(author)).thenReturn(authorResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(RESOURCE_URL + "/" + authorId)
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(authorResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Valda"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("35033 Cronin Overpass, Shanitastad, AZ 24273-3513"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("valda@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.telephone").value("+375291234568"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.registrationDate").value("2023-11-17T15:00:00"));

        verify(authorService, times(1)).findById(authorId);
    }

    @Test
    void shouldSaveAuthor() throws Exception {
        //given
        AuthorDto author = AuthorTestData.builder().build().buildAuthorDto();
        AuthorDto authorSaved = AuthorTestData.builder().build().buildAuthorDto();
        AuthorResponse authorResponse = AuthorTestData.builder().build().buildAuthorResponse();
        AuthorRequest authorRequest = AuthorTestData.builder().build().buildAuthorRequest();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForAuthorOperationCRUD(any());
        when(authorMapper.requestToDto(authorRequest)).thenReturn(author);
        when(authorService.save(author)).thenReturn(authorSaved);
        when(authorMapper.dtoToResponse(authorSaved)).thenReturn(authorResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(RESOURCE_URL)
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(authorResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Valda"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("35033 Cronin Overpass, Shanitastad, AZ 24273-3513"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("valda@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.telephone").value("+375291234568"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.registrationDate").value("2023-11-17T15:00:00"));

        verify(authorService, times(1)).save(author);
    }

    @Test
    void shouldFailSave() throws Exception {
        //given
        AuthorDto author = AuthorTestData.builder().build().buildAuthorDto();
        AuthorDto authorSaved = AuthorTestData.builder().build().buildAuthorDto();
        AuthorResponse authorResponse = AuthorTestData.builder().build().buildAuthorResponse();
        AuthorRequest authorRequest = AuthorTestData.builder().build().buildAuthorRequest();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForAuthorOperationCRUD(any());
        when(authorMapper.requestToDto(authorRequest)).thenReturn(author);
        when(authorService.save(author)).thenReturn(authorSaved);
        when(authorMapper.dtoToResponse(authorSaved)).thenReturn(authorResponse);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RESOURCE_URL)
                                .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        verify(authorService, never()).save(author);
    }

}