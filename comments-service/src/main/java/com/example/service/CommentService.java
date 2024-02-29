package com.example.service;

import com.example.controller.request.CommentRequest;
import com.example.entity.dto.CommentDto;
import org.springframework.data.domain.Page;

public interface CommentService {

    CommentDto save(Long newsId, CommentRequest commentRequest);

    void deleteById(Long newsId, Long commentId);

    CommentDto update(Long newsId, Long commentId, CommentRequest commentRequest);

    CommentDto findById(Long newsId, Long commentId);

    Page<CommentDto> findAllCommentsByNewsId(Long newsId, Integer page, Integer size, String orderBy, String direction);
}
