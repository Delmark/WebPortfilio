package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.service.interfaces.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<User> findUserById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }
}