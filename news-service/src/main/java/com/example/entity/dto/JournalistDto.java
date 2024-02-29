package com.example.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalistDto implements Serializable {

    private Long id;
    private String username;
    private String address;
    private String email;
    private String telephone;
    private LocalDateTime registrationDate;

}
