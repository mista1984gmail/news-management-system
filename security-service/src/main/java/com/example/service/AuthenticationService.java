package com.example.service;


import com.example.dao.request.SignUpRequest;
import com.example.dao.request.SigninRequest;
import com.example.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {

    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);
}
