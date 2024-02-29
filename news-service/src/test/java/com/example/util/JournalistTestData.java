package com.example.util;

import com.example.controller.request.JournalistRequest;
import com.example.controller.response.JournalistResponse;
import com.example.entity.Journalist;
import com.example.entity.News;
import com.example.entity.dto.JournalistDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(setterPrefix = "with")
public class JournalistTestData {

    @Builder.Default
    private Long id = ConstantsForTest.JOURNALIST_ID;

    @Builder.Default
    private String username = ConstantsForTest.JOURNALIST_USERNAME;

    @Builder.Default
    private String address = ConstantsForTest.JOURNALIST_ADDRESS;

    @Builder.Default
    private String email = ConstantsForTest.JOURNALIST_EMAIL;

    @Builder.Default
    private String telephone = ConstantsForTest.JOURNALIST_TELEPHONE;

    @Builder.Default
    private LocalDateTime registrationDate = ConstantsForTest.JOURNALIST_REGISTRATION_DATE;

    @Builder.Default
    private boolean isBlocked = Boolean.FALSE;

    @Builder.Default
    private boolean deleted = Boolean.FALSE;

    @Builder.Default
    private List<News> news = new ArrayList<>();

    public Journalist buildJournalist() {
        return new Journalist(id, username, address, email, telephone, registrationDate, isBlocked, deleted, news);
    }

    public JournalistDto buildJournalistDto() {
        return new JournalistDto(id, username, address, email, telephone, registrationDate);
    }
    public JournalistRequest buildJournalistRequest() {
        return new JournalistRequest(username, address, email, telephone);
    }
    public JournalistResponse buildJournalistResponse() {
        return new JournalistResponse(username, address, email, telephone, registrationDate);}

}
