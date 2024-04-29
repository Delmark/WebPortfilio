package com.delmark.portfoilo.controller;

import com.delmark.portfoilo.models.Techs;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tech")
public class TechController {

    @GetMapping
    public ResponseEntity<Page<Techs>> getAllPages() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Techs> getTechById(@PathVariable Long id) {
        return null;
    }

    @PostMapping ResponseEntity<Techs> createTech(@RequestBody Techs techs) {
        return null;
    }

    @PutMapping ResponseEntity<Techs> editTech(@RequestBody Techs techs) {
        return null;
    }

    @DeleteMapping ("/{id}")
    ResponseEntity<Void> deleteTech(@PathVariable Long id) {
        return null;
    }
}
