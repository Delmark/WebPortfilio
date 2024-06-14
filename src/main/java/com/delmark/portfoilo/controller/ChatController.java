package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.DTO.ChatCreationDTO;
import com.delmark.portfoilo.models.messages.Chat;
import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.service.interfaces.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Chats", description = "API for managing chats")
@RequestMapping("/api/v1/chat")
public class ChatController {

    private ChatService chatService;

    @GetMapping
    public ResponseEntity<Page<Chat>> getAllChats(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        return ResponseEntity.ok(chatService.getAllChats(page, size));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Page<Chat>> getAllUserChats(
            @PathVariable("username") String username,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return ResponseEntity.ok(chatService.getAllUserChats(username, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getChatById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(chatService.getChatById(id));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<Message>> getChatMessages(
            @PathVariable("id") Long id,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ) {
        return ResponseEntity.ok(chatService.getMessagesByChatId(id, page, size));
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody ChatCreationDTO dto) {
        return ResponseEntity.ok(chatService.createChat(dto));
    }

    @PutMapping("/{chatId}/user/{userId}")
    public ResponseEntity<Chat> addUserToChat(@PathVariable("chatId") Long chatId, @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(chatService.addUserToChat(userId, chatId));
    }

    @DeleteMapping("/{chatId}/user/{userId}")
    public ResponseEntity<Void> removeUserFromChat(@PathVariable("chatId") Long chatId, @PathVariable("userId") Long userId) {
        chatService.removeUserFromChat(userId, chatId);
        return ResponseEntity.ok().build();
    }

}
