package com.example.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto implements Serializable {

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String text;
    private AuthorDto author;

}
