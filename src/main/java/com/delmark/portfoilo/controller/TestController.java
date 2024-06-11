package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.service.interfaces.UserService;
import liquibase.resource.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test")
@AllArgsConstructor
@Slf4j
public class TestController {

    private UserService userService;

    @GetMapping("/getImage")
    public ResponseEntity<byte[]> getPlaceholderImage() throws IOException {

        byte[] image = userService.getAllUsers(0).get().collect(Collectors.toList()).get(0).getAvatar();
        String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(image));
        log.info("Mime Type is {}", mimeType);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }

    @GetMapping("/getImage/{username}")
    public ResponseEntity<byte[]> getAvatarImageByUsername(@PathVariable(value = "username") String username) throws IOException {
        byte[] image = userService.getAllUsers(0).get().filter(user -> user.getUsername().equals(username)).collect(Collectors.toList()).getFirst().getAvatar();
        String mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(image));
        log.info("Mime Type is {}", mimeType);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
    }
}
