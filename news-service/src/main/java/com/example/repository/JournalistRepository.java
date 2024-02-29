package com.example.repository;

import com.example.entity.Journalist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JournalistRepository extends JpaRepository<Journalist, Long> {

    Optional<Journalist> findByUsername(String username);

}
