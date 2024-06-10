package com.delmark.portfoilo.service.interfaces;

import com.delmark.portfoilo.models.DTO.authorization.JwtTokenDTO;
import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateJWT(Authentication auth);

    JwtTokenDTO provideToken(String username, String password);
    JwtTokenDTO provideToken(Authentication authentication);
}
