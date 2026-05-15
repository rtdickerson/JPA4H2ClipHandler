package com.example.cliphandler.service;

import com.example.cliphandler.dto.ClipboardItemDto;

import java.util.List;
import java.util.Optional;

public interface ClipboardService {
    List<ClipboardItemDto> findAll();
    List<ClipboardItemDto> findByUsername(String username);
    Optional<ClipboardItemDto> findByUsernameAndName(String username, String name);
    ClipboardItemDto upsert(String username, String name, String content, boolean isFile);
    void delete(String username, String name);
    void deleteByUsername(String username);
}
