package com.example.security;

public interface SecurityService {

    User getPayload(String token);
    void checkAccessForAuthorOperationCRUD(String token);
    void checkAccessForSaveComments(String token);
    void checkAccessForDeleteAndUpdateComments(String token, Long newsId, Long commentId);

}
