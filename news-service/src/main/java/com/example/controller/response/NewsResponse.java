package com.example.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {

    private Long id;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String title;
    private String text;
    private String journalistName;

}
