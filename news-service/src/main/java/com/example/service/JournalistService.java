package com.example.service;

import com.example.entity.Journalist;
import com.example.entity.dto.AuthorDto;
import com.example.entity.dto.JournalistDto;
import org.springframework.data.domain.Page;

public interface JournalistService {

    Page<JournalistDto> findAll(Integer page, Integer size, String orderBy, String direction);

    JournalistDto save(JournalistDto journalist);

    void deleteById(Long id);

    JournalistDto update(JournalistDto journalist);

    JournalistDto findById(Long id);

    Journalist findByUsername(String username);

    void blocked(Long id);
}
