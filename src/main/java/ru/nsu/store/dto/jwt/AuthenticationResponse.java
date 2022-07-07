package ru.nsu.store.dto.jwt;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String email;
    private String token;
    private String userRole;
}
