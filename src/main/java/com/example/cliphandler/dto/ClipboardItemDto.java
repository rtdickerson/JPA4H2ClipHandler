package com.example.cliphandler.dto;

import com.example.cliphandler.entity.ClipboardItem;
import java.time.LocalDateTime;

public record ClipboardItemDto(
        Long id,
        String username,
        String name,
        String content,
        boolean file,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ClipboardItemDto from(ClipboardItem item) {
        return new ClipboardItemDto(
                item.getId(),
                item.getUsername(),
                item.getName(),
                item.getContent(),
                item.isFile(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    public String contentPreview() {
        if (file) return "[binary file — base64 encoded]";
        if (content == null || content.isEmpty()) return "";
        return content.length() > 120 ? content.substring(0, 120) + "…" : content;
    }
}
