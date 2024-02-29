package com.example;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorResponse {

    private final int statusCode;
    private final String message;
    private Date timestamp;

}
