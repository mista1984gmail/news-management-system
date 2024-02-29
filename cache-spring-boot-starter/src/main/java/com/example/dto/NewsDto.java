package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDto implements Serializable {

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String title;
    private String text;
    private JournalistDto journalist;

}
