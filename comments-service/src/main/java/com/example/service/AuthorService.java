package com.example.service;

import com.example.entity.Author;
import com.example.entity.dto.AuthorDto;
import org.springframework.data.domain.Page;

public interface AuthorService {

    Page<AuthorDto> findAll(Integer page, Integer size, String orderBy, String direction);

    AuthorDto save(AuthorDto author);

    void deleteById(Long id);

    AuthorDto update(AuthorDto author);

    AuthorDto findById(Long id);

    Author findByUsername(String username);

    void blocked(Long id);
}
