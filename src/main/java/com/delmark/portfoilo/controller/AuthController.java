package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.JwtTokenDTO;
import com.delmark.portfoilo.models.DTO.UserDto;
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
    public ResponseEntity<Void> registerRestUser(@RequestBody @Valid UserDto registrationDto) {
        userService.registration(registrationDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getToken")
    public ResponseEntity<JwtTokenDTO> getToken(@RequestBody @Valid UserDto dto) {
        JwtTokenDTO jwtTokenDTO = tokenService.provideToken(dto.getUsername(), dto.getPassword());
        // TODO: Сделать исключение
        if (jwtTokenDTO.getToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        else {
            return ResponseEntity.ok(jwtTokenDTO);
        }
    }
}