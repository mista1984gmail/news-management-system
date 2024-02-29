package com.example.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {

    @NotNull
    @Size(min = 2, max = 50, message
            = "Title must be between 2 and 50 characters long")
    private String title;

    @NotNull
    @Size(min = 2, max = 1280, message
            = "Text must be between 2 and 1280 characters long")
    private String text;

    @NotNull
    @Size(min = 2, max = 255, message
            = "Username must be between 2 and 1280 characters long")
    private String username;

}
