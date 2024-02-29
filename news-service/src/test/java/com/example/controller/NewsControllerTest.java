package com.example.controller;

import com.example.controller.request.JournalistRequest;
import com.example.controller.request.NewsRequest;
import com.example.controller.response.JournalistResponse;
import com.example.controller.response.NewsResponse;
import com.example.entity.dto.JournalistDto;
import com.example.entity.dto.NewsDto;
import com.example.mapper.NewsMapper;
import com.example.security.SecurityService;
import com.example.service.NewsService;
import com.example.util.ConstantsForTest;
import com.example.util.JournalistTestData;
import com.example.util.NewsTestData;
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

@WebMvcTest(NewsController.class)
class NewsControllerTest {

    private final String RESOURCE_URL = "/api/v1/news";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NewsService newsService;

    @MockBean
    private NewsMapper newsMapper;

    @MockBean
    private SecurityService securityService;

    @Test
    void shouldGetNewsById() throws Exception {
        //given
        Long newsId = ConstantsForTest.NEWS_ID;
        NewsDto news = NewsTestData.builder().build().buildNewsDto();
        NewsResponse newsResponse = NewsTestData.builder().build().buildNewsResponse();

        //when
        when(newsService.findById(newsId)).thenReturn(news);
        when(newsMapper.dtoToResponse(news)).thenReturn(newsResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.get(RESOURCE_URL + "/" + newsId)
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(newsResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.createDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updateDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Eget velit aliquet sagittis id."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("Lorem dolor sed viverra ipsum nunc aliquet bibendum enim facilisis gravida neque convallis a cras semper auctor neque vitae tempus quam pellentesque nec nam aliquam sem et tortor consequat id porta nibh venenatis cras sed felis eget velit aliquet sagittis id consectetur purus ut faucibus pulvinar elementum integer enim neque."));

        verify(newsService, times(1)).findById(newsId);
    }

    @Test
    void shouldSaveNews() throws Exception {
        //given
        NewsDto news = NewsTestData.builder().build().buildNewsDto();
        NewsDto newsSaved = NewsTestData.builder().build().buildNewsDto();
        NewsResponse newsResponse = NewsTestData.builder().build().buildNewsResponse();
        NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForSaveNews(any());

        when(newsService.save(newsRequest)).thenReturn(newsSaved);
        when(newsMapper.dtoToResponse(newsSaved)).thenReturn(newsResponse);

        //then
        mockMvc.perform(MockMvcRequestBuilders.post(RESOURCE_URL)
                        .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                        .content(objectMapper.findAndRegisterModules().writeValueAsString(newsResponse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.createDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updateDate").value("2024-02-27T15:00:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Eget velit aliquet sagittis id."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value("Lorem dolor sed viverra ipsum nunc aliquet bibendum enim facilisis gravida neque convallis a cras semper auctor neque vitae tempus quam pellentesque nec nam aliquam sem et tortor consequat id porta nibh venenatis cras sed felis eget velit aliquet sagittis id consectetur purus ut faucibus pulvinar elementum integer enim neque."));

        verify(newsService, times(1)).save(newsRequest);
    }

    @Test
    void shouldFailSave() throws Exception {
        //given
        NewsDto news = NewsTestData.builder().build().buildNewsDto();
        NewsDto newsSaved = NewsTestData.builder().build().buildNewsDto();
        NewsResponse newsResponse = NewsTestData.builder().build().buildNewsResponse();
        NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();

        //when
        doNothing()
                .when(securityService)
                .checkAccessForSaveNews(any());

        when(newsService.save(newsRequest)).thenReturn(newsSaved);
        when(newsMapper.dtoToResponse(newsSaved)).thenReturn(newsResponse);

        //then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(RESOURCE_URL)
                                .header("token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXZhbm5hQGdtYWlsLmNvbSIsImlhdCI6MTcwODY5ODM5NywiZXhwIjoxNzA4Njk5ODM3LCJyb2xlcyI6WyJBRE1JTiJdfQ.YnkR_c4STdJ16IdKFG0QQkLDx0lGG6k1GeEEJ_zxh14")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().is4xxClientError())
                .andDo(print());

        verify(newsService, never()).save(newsRequest);
    }
}