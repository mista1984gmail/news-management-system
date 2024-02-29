package com.example.security;

import com.example.security.model.User;

public interface SecurityService {

    User getPayload(String token);
    void checkAccessForJournalistOperationCRUD(String token);
    void checkAccessForSaveNews(String token);
    void checkAccessForDeleteAndUpdateNews(String token, Long newsId);

}
