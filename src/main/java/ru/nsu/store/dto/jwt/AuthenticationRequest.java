package ru.nsu.store.dto.jwt;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String email;
    private String password;
}
