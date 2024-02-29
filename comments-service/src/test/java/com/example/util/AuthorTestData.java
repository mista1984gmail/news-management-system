package com.example.util;

import com.example.controller.request.AuthorRequest;
import com.example.controller.response.AuthorResponse;
import com.example.entity.Author;
import com.example.entity.Comment;
import com.example.entity.dto.AuthorDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
public class AuthorTestData {

    @Builder.Default
    private Long id = ConstantsForTest.AUTHOR_ID;

    @Builder.Default
    private String username = ConstantsForTest.AUTHOR_USERNAME;

    @Builder.Default
    private String address = ConstantsForTest.AUTHOR_ADDRESS;

    @Builder.Default
    private String email = ConstantsForTest.AUTHOR_EMAIL;

    @Builder.Default
    private String telephone = ConstantsForTest.AUTHOR_TELEPHONE;

    @Builder.Default
    private LocalDateTime registrationDate = ConstantsForTest.AUTHOR_REGISTRATION_DATE;

    @Builder.Default
    private boolean isBlocked = Boolean.FALSE;

    @Builder.Default
    private boolean deleted = Boolean.FALSE;

    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public Author buildAuthor() {
        return new Author(id, username, address, email, telephone, registrationDate, isBlocked, deleted, comments);
    }

    public AuthorDto buildAuthorDto() {
        return new AuthorDto(id, username, address, email, telephone, registrationDate);
    }
    public AuthorRequest buildAuthorRequest() {
        return new AuthorRequest(username, address, email, telephone);
    }
    public AuthorResponse buildAuthorResponse() {
        return new AuthorResponse(username, address, email, telephone, registrationDate);
    }

}
