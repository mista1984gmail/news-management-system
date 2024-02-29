package com.example.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class CommentRequest {

    @NotNull
    @Size(min = 2, max = 1280, message
            = "Text must be between 2 and 1280 characters long")
    private String text;

    @NotNull
    @Size(min = 2, max = 255, message
            = "Username must be between 2 and 1280 characters long")
    private String username;

}
