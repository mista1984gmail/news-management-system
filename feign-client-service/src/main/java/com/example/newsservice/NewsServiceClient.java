package com.example.newsservice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "news-service", url = "http://localhost:8081", path = "/api/v1/news")
public interface NewsServiceClient {

    @GetMapping(path = "/{newsId}")
    NewsResponse getNewsById(@PathVariable("newsId") Long newsId);

}
