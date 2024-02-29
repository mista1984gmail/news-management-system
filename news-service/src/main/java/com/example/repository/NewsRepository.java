package com.example.repository;

import com.example.entity.Journalist;
import com.example.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAllByJournalist(Journalist journalist);
}
