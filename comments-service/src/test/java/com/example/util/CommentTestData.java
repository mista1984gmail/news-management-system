package com.example.util;

import com.example.controller.request.CommentRequest;
import com.example.controller.response.CommentResponse;
import com.example.entity.Author;
import com.example.entity.Comment;
import com.example.entity.dto.CommentDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder(setterPrefix = "with")
public class CommentTestData {

    @Builder.Default
    private Long id = ConstantsForTest.COMMENT_ID;

    @Builder.Default
    private LocalDateTime createDate = ConstantsForTest.COMMENT_CREATE_DATE;

    @Builder.Default
    private LocalDateTime updateDate = ConstantsForTest.COMMENT_UPDATE_DATE;

    @Builder.Default
    private String text = ConstantsForTest.COMMENT_TEXT;

    @Builder.Default
    private Long newsId = ConstantsForTest.NEWS_ID;

    @Builder.Default
    private boolean deleted = Boolean.FALSE;

    @Builder.Default
    private Author author = AuthorTestData.builder().build().buildAuthor();

    public Comment buildComment() {
        return new Comment(id, createDate, updateDate, text, newsId, deleted, author);
    }
    public CommentDto buildCommentDto() {
        return new CommentDto(id, createDate, updateDate, text, AuthorTestData.builder().build().buildAuthorDto());
    }
    public CommentRequest buildCommentRequest() {
        return new CommentRequest(text, ConstantsForTest.AUTHOR_USERNAME.toLowerCase());
    }

    public CommentResponse buildCommentResponse() {
        return new CommentResponse(id, createDate, updateDate, text, author.getUsername());
    }
}
