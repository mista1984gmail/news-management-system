package com.example.service;

import com.example.controller.request.NewsRequest;
import com.example.entity.News;
import com.example.entity.dto.NewsDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NewsService {

    NewsDto save(NewsRequest newsRequest);

    void deleteById(Long id);

    NewsDto update(Long id, NewsRequest newsRequest);

    NewsDto findById(Long id);

    Page<NewsDto> findAllWithPaginationAndSorting(Integer page, Integer size, String orderBy, String direction);

    List<NewsDto> findAllByJournalist(Long journalistId);

}
