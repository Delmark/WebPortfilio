package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.JwtTokenDTO;
import com.delmark.portfoilo.models.DTO.UserDto;
import com.delmark.portfoilo.service.interfaces.TokenService;
import com.delmark.portfoilo.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody UserDto registrationDto) {
        userService.registration(registrationDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/getToken")
    public ResponseEntity<JwtTokenDTO> getToken(@RequestBody UserDto dto) {
        JwtTokenDTO jwtTokenDTO = tokenService.provideToken(dto.getUsername(), dto.getPassword());
        // TODO: Сделать исключение
        if (jwtTokenDTO.getToken() == null) {
            return ResponseEntity.badRequest().build();
        }
        else {
            return ResponseEntity.ok(jwtTokenDTO);
        }
    }
}