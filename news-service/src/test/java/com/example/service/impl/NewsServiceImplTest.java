package com.example.service.impl;

import com.example.controller.request.NewsRequest;
import com.example.entity.Journalist;
import com.example.entity.News;
import com.example.entity.dto.JournalistDto;
import com.example.entity.dto.NewsDto;
import com.example.exception.EntityNotFoundException;
import com.example.mapper.JournalistMapper;
import com.example.mapper.NewsMapper;
import com.example.repository.NewsRepository;
import com.example.service.JournalistService;
import com.example.util.ConstantsForTest;
import com.example.util.JournalistTestData;
import com.example.util.NewsTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.N;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private NewsRepository newsRepository;

    @Mock
    private JournalistService journalistService;

    @Mock
    private NewsMapper newsMapper;

    @Mock
    private JournalistMapper journalistMapper;

    @InjectMocks
    private NewsServiceImpl newsService;

    @Test
    void shouldDeleteNews(){
        // given
        Long id = ConstantsForTest.NEWS_ID;
        Optional<News> news = Optional.of(NewsTestData.builder()
                .build()
                .buildNews());

        when(newsRepository.findById(id))
                .thenReturn(news);
        doNothing()
                .when(newsRepository)
                .delete(news.get());

        //when
        newsService.deleteById(id);

        //then
        verify(newsRepository).delete(any());
    }

    @Test
    void shouldNotDeleteJournalistAndThrowsNewsNotFoundException() {
        // given
        Long id = ConstantsForTest.NEWS_ID;
        String errorMessage = "News with id: " + id + " not found";
        Optional<News> news = Optional.of(NewsTestData.builder()
                .withId(null)
                .build()
                .buildNews());

        when(newsRepository.findById(id))
                .thenThrow(new EntityNotFoundException(News.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            newsService.deleteById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
        verify(newsRepository, never()).delete(news.get());
    }

     @Test
    void shouldNotGetNewsByIdAndThrowsNewsNotFoundException() {
        // given
         Long id = ConstantsForTest.NEWS_ID;
         String errorMessage = "News with id: " + id + " not found";

        when(newsRepository.findById(id))
                .thenThrow(new EntityNotFoundException(News.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            newsService.findById(id);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }

    @Test
    void shouldGetNewsById() {
        // given
        Long id = ConstantsForTest.NEWS_ID;
        Optional<News> news = Optional.of(NewsTestData.builder()
                .withId(null)
                .build()
                .buildNews());
        NewsDto expected = NewsTestData.builder()
                .build()
                .buildNewsDto();

        when(newsRepository.findById(id))
                .thenReturn(news);
        when(newsMapper.entityToDto(news.get()))
                .thenReturn(expected);

        //when
        NewsDto actual = newsService.findById(id);

        //then
        assertEquals(expected, actual);
        verify(newsRepository).findById(id);
        verify(newsMapper).entityToDto(news.get());
    }

    @Test
    void shouldCreateNews() {
        // given
        NewsRequest newsRequest = NewsTestData.builder().build().buildNewsRequest();
        NewsDto expected = NewsTestData.builder()
                .build()
                .buildNewsDto();
        News news = NewsTestData.builder()
                .build()
                .buildNews();
        Journalist journalist = JournalistTestData.builder()
                .build()
                .buildJournalist();

        when(journalistService.findByUsername(newsRequest.getUsername()))
                .thenReturn(journalist);
        when(newsRepository.save(any()))
                .thenReturn(news);
        when(newsMapper.entityToDto(news))
                .thenReturn(expected);


        //when
        NewsDto actual = newsService.save(newsRequest);

        //then
        verify(newsRepository).save(any());
        assertEquals(expected, actual);

    }

    @Test
    void shouldUpdateNews() {
        // given
        NewsRequest newsRequest = NewsTestData.builder()
                .withText("New text.")
                .build().buildNewsRequest();
        NewsDto newsDtoForUpdate = NewsTestData.builder()
                .withText("New text.")
                .build()
                .buildNewsDto();
        Optional<News> optionalNewsFromDB = Optional.of(NewsTestData.builder()
                .build()
                .buildNews());
        News news = NewsTestData.builder()
                .withText("New text.")
                .build()
                .buildNews();
        NewsDto newsFromDB = NewsTestData.builder()
                .withText("New text.")
                .build()
                .buildNewsDto();
        Journalist journalist = JournalistTestData.builder()
                .build()
                .buildJournalist();
        Long id = ConstantsForTest.NEWS_ID;

        when(journalistService.findByUsername(newsRequest.getUsername()))
                .thenReturn(journalist);
        when(newsRepository.findById(id)).thenReturn(optionalNewsFromDB);
        when(newsRepository.save(any())).thenReturn(news);
        when(newsMapper.entityToDto(news))
                .thenReturn(newsFromDB);

        //when
        NewsDto expected = newsService.update(id, newsRequest);

        //then
        verify(newsRepository).save(any());

        assertThat(newsDtoForUpdate)
                .hasFieldOrPropertyWithValue(News.Fields.text, expected.getText())
                .hasFieldOrPropertyWithValue(News.Fields.title, expected.getTitle())
                .hasFieldOrPropertyWithValue(News.Fields.id, expected.getId());
    }

    @Test
    void shouldNotUpdateNewsAndThrowsNewsNotFoundException() {
        // given
        Long id = ConstantsForTest.NEWS_ID;
        NewsRequest newsRequest = NewsTestData.builder()
                .withText("New text.")
                .build().buildNewsRequest();
        String errorMessage = "News with id: " + id + " not found";

        when(newsRepository.findById(id))
                .thenThrow(new EntityNotFoundException (News.class, id));

        //when
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            newsService.update(id, newsRequest);
        });

        //then
        assertEquals(errorMessage, thrown.getMessage());
    }


}