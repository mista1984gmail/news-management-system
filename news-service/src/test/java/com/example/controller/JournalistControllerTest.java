package com.example.controller;

import com.example.controller.request.JournalistRequest;
import com.example.controller.response.JournalistResponse;
import com.example.entity.dto.JournalistDto;
import com.example.mapper.JournalistMapper;
import com.example.mapper.NewsMapper;
import com.example.security.SecurityService;
import com.example.service.JournalistService;
import com.example.service.NewsService;
import com.example.util.ConstantsForTest;
import com.example.util.JournalistTestData;
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
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JournalistController.class)
class JournalistControllerTest {

    private final String RESOURCE_URL = "/api/v1/journalists";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JournalistService journalistService;

    @MockBean
    private JournalistMapper journalistMapper;

    @MockBean
    private NewsService newsService;

    @MockBean
    private NewsMapper newsMapper;

    @MockBean
    private SecurityService securityService;

    @Test
    void shouldGetJournalistById() throws Exception {
        //given
        Long journalistId = ConstantsForTest.JOURNALIST_ID;
        JournalistDto journalist = JournalistTestData.builder().build().buildJournalistDto();
        JournalistResponse journalistResponse = JournalistTestData.builder().build().buildJournalistResponse();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForJournalistOperationCRUD(any());
        when(journalistService.findById(journalistId)).thenReturn(journalist);
        when(journalistMapper.dtoToResponse(journalist)).thenReturn(journalistResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(RESOURCE_URL + "/" + journalistId)
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(journalistResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("anisa"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("25933 Stamm Fort, East Dwightshire, MO 84762"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("anisa@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.telephone").value("+375291234512"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.registrationDate").value("2024-02-27T15:00:00"));

        verify(journalistService, times(1)).findById(journalistId);
    }

        @Test
        void shouldSaveJournalist() throws Exception {
        //given
        JournalistDto journalist = JournalistTestData.builder().build().buildJournalistDto();
        JournalistDto journalistSaved = JournalistTestData.builder().build().buildJournalistDto();
        JournalistResponse journalistResponse = JournalistTestData.builder().build().buildJournalistResponse();
        JournalistRequest journalistRequest = JournalistTestData.builder().build().buildJournalistRequest();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForJournalistOperationCRUD(any());
        when(journalistMapper.requestToDto(journalistRequest)).thenReturn(journalist);
        when(journalistService.save(journalist)).thenReturn(journalistSaved);
        when(journalistMapper.dtoToResponse(journalistSaved)).thenReturn(journalistResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(RESOURCE_URL)
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(journalistResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("anisa"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.address").value("25933 Stamm Fort, East Dwightshire, MO 84762"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("anisa@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.telephone").value("+375291234512"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.registrationDate").value("2024-02-27T15:00:00"));

        verify(journalistService, times(1)).save(journalist);
    }

    @Test
    void shouldFailSave() throws Exception {
        //given
        JournalistDto journalist = JournalistTestData.builder().build().buildJournalistDto();
        JournalistDto journalistSaved = JournalistTestData.builder().build().buildJournalistDto();
        JournalistResponse journalistResponse = JournalistTestData.builder().build().buildJournalistResponse();
        JournalistRequest journalistRequest = JournalistTestData.builder().build().buildJournalistRequest();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForJournalistOperationCRUD(any());
        when(journalistMapper.requestToDto(journalistRequest)).thenReturn(journalist);
        when(journalistService.save(journalist)).thenReturn(journalistSaved);
        when(journalistMapper.dtoToResponse(journalistSaved)).thenReturn(journalistResponse);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RESOURCE_URL)
                                .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        verify(journalistService, never()).save(journalist);
    }
}