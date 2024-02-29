package com.example.exception;

public class CommentNotCorrespondsToNewException extends ApplicationException {

    public CommentNotCorrespondsToNewException(Long commentId, Long newsId) {
        super("Comment with id: " + commentId + " not corresponds to news " + newsId);
    }

}
