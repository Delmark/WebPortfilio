package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.user.User;
import com.delmark.portfoilo.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Hidden
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    UserService userService;

    @GetMapping("/allUsers")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(name = "page") Integer page) {
        return ResponseEntity.ok(userService.getAllUsers(page));
    }

    @PutMapping("/grant/{username}")
    public ResponseEntity<User> grantRoleToUser(@PathVariable(name = "username") String username,
                                                @RequestParam(name = "role") String role) {
        return ResponseEntity.ok(userService.grantAuthority(role, username));
    }

    @PutMapping("/revoke/{username}")
    public ResponseEntity<User> revokeRoleFromUser(@PathVariable(name = "username") String username,
                                                   @RequestParam(name = "role") String role) {
        return ResponseEntity.ok(userService.revokeAuthority(role, username));
    }
}
