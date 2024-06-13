package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.messages.Message;
import com.delmark.portfoilo.service.interfaces.MessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "Chat Messages", description = "Chat Messages API")
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(
            @RequestParam("chatId") Long chatId,
            @RequestParam("message") String message
    ) {
        return ResponseEntity.ok(messageService.createMessage(chatId, message));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> editMessage(
            @PathVariable("id") Long id,
            @RequestParam("message") String message
    ) {
        return ResponseEntity.ok(messageService.editMessageById(id, message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long id) {
        messageService.deleteMessageById(id);
        return ResponseEntity.ok().build();
    }
}
