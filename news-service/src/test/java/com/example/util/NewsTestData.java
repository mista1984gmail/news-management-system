package com.example.util;

import com.example.controller.request.JournalistRequest;
import com.example.controller.request.NewsRequest;
import com.example.controller.response.JournalistResponse;
import com.example.controller.response.NewsResponse;
import com.example.entity.Journalist;
import com.example.entity.News;
import com.example.entity.dto.CommentDto;
import com.example.entity.dto.JournalistDto;
import com.example.entity.dto.NewsDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
public class NewsTestData {

    @Builder.Default
    private Long id = ConstantsForTest.NEWS_ID;

    @Builder.Default
    private LocalDateTime createDate = ConstantsForTest.NEWS_CREATE_DATE;

    @Builder.Default
    private LocalDateTime updateDate = ConstantsForTest.NEWS_UPDATE_DATE;

    @Builder.Default
    private String title = ConstantsForTest.NEWS_TITLE;

    @Builder.Default
    private String text = ConstantsForTest.NEWS_TEXT;

    @Builder.Default
    private boolean deleted = Boolean.FALSE;

    @Builder.Default
    private List<CommentDto> comments = new ArrayList<>();

    @Builder.Default
    private Journalist journalist = JournalistTestData.builder().build().buildJournalist();

    public News buildNews() {
        return new News(id, createDate, createDate, title, text, deleted, comments, journalist);
    }

    public NewsDto buildNewsDto() {
        return new NewsDto(id, createDate, createDate, title, text, JournalistTestData.builder().build().buildJournalistDto());
    }
    public NewsRequest buildNewsRequest() {
        return new NewsRequest(title, text, ConstantsForTest.JOURNALIST_USERNAME);
    }
    public NewsResponse buildNewsResponse() {
        return new NewsResponse(id, createDate, createDate, title, text, ConstantsForTest.JOURNALIST_USERNAME);}

}
