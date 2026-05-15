package com.example.cliphandler.controller;

import com.example.cliphandler.dto.ClipboardItemDto;
import com.example.cliphandler.dto.ClipboardItemRequest;
import com.example.cliphandler.service.ClipboardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clipboard")
public class ClipboardApiController {

    private final ClipboardService service;

    public ClipboardApiController(ClipboardService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClipboardItemDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{username}")
    public List<ClipboardItemDto> findByUsername(@PathVariable String username) {
        return service.findByUsername(username);
    }

    @GetMapping("/{username}/{name}")
    public ResponseEntity<ClipboardItemDto> findOne(
            @PathVariable String username,
            @PathVariable String name) {
        return service.findByUsernameAndName(username, name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{username}/{name}")
    public ClipboardItemDto upsert(
            @PathVariable String username,
            @PathVariable String name,
            @Valid @RequestBody ClipboardItemRequest request) {
        return service.upsert(username, name, request.content(), request.file());
    }

    @DeleteMapping("/{username}/{name}")
    public ResponseEntity<Void> delete(
            @PathVariable String username,
            @PathVariable String name) {
        service.delete(username, name);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteByUsername(@PathVariable String username) {
        service.deleteByUsername(username);
        return ResponseEntity.noContent().build();
    }
}
