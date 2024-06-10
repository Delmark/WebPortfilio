package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.authorization.JwtTokenDTO;
import com.delmark.portfoilo.models.DTO.authorization.UserAuthDto;
import com.delmark.portfoilo.models.DTO.authorization.UserRegDto;
import com.delmark.portfoilo.service.interfaces.TokenService;
import com.delmark.portfoilo.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private UserService userService;
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid UserRegDto registrationDto) {
        userService.registration(registrationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getToken")
    public ResponseEntity<JwtTokenDTO> getToken(@RequestBody @Valid UserAuthDto dto) {
        JwtTokenDTO jwtTokenDTO = tokenService.provideToken(dto.getUsername(), dto.getPassword());

        if (jwtTokenDTO.getToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else {
            return ResponseEntity.ok(jwtTokenDTO);
        }
    }
}