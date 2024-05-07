package com.delmark.portfoilo.service.implementations;

import com.delmark.portfoilo.models.DTO.JwtTokenDTO;
import com.delmark.portfoilo.models.User;
import com.delmark.portfoilo.repository.UserRepository;
import com.delmark.portfoilo.service.interfaces.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {
    private JwtEncoder jwtEncoder;

    private AuthenticationManager authenticationManager;

    @Override
    public String generateJWT(Authentication auth) {
        Instant now = Instant.now();

        String roleScope = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .subject(auth.getName())
                .claim("authorities", roleScope)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public JwtTokenDTO provideToken(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String token = generateJWT(authentication);

            return new JwtTokenDTO(token);
        }
        catch (AuthenticationException e) {
            return new JwtTokenDTO(null);
        }
    }

    @Override
    public JwtTokenDTO provideToken(Authentication authenticationFromUser) {
        try {
            String token = generateJWT(authenticationFromUser);

            return new JwtTokenDTO(token);
        }
        catch (AuthenticationException e) {
            return new JwtTokenDTO(null);
        }
    }
}
