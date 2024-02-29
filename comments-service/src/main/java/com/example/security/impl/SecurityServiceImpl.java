package com.example.security.impl;

import com.example.entity.dto.AuthorDto;
import com.example.entity.dto.CommentDto;
import com.example.exception.NoAccessException;
import com.example.exception.TokenIsNotValidException;
import com.example.exception.WrongAuthorException;
import com.example.security.JwtUtil;
import com.example.security.SecurityService;
import com.example.security.User;
import com.example.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final JwtUtil jwtUtil;
    private final CommentService commentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public User getPayload(String token) {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        User user =null;
        try {
            user = objectMapper.readValue(payload, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void checkAccessForAuthorOperationCRUD(String token) {
        try {
            jwtUtil.validateToken(token);
        } catch (Exception exception){
            throw new TokenIsNotValidException();
        }
        User user = getPayload(token);
        if(!user.getRoles().contains("ADMIN")){
            throw new NoAccessException();
        }
    }

    @Override
    public void checkAccessForSaveComments(String token) {
        try {
            jwtUtil.validateToken(token);
        } catch (Exception exception){
            throw new TokenIsNotValidException();
        }
        User user = getPayload(token);
        System.out.println(user);
        if(user.getRoles().contains("SUBSCRIBER")){
        } else {
            throw new NoAccessException();
        }
    }

    @Override
    public void checkAccessForDeleteAndUpdateComments(String token, Long newsId, Long commentId) {
        try {
            jwtUtil.validateToken(token);
        } catch (Exception exception){
            throw new TokenIsNotValidException();
        }
        User user = getPayload(token);
        if(user.getRoles().contains("SUBSCRIBER") || user.getRoles().contains("ADMIN")){
            if(user.getRoles().contains("SUBSCRIBER")){
                CommentDto comment = commentService.findById(newsId, commentId);
                AuthorDto author = comment.getAuthor();
                if(!author.getEmail().equals(user.getSub())){
                    throw new WrongAuthorException();
                }}
            else {
            }
        } else {
            throw new NoAccessException();
        }
    }

}
