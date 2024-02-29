package com.example.controller.request;

import com.example.controller.validator.PhoneNumber;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalistRequest {

    @NotNull
    private String username;

    @NotNull
    private String address;

    @NotNull
    @Email
    private String email;

    @NotNull
    @PhoneNumber
    private String telephone;

}