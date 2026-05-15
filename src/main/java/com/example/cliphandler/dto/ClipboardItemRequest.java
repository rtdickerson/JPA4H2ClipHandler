package com.example.cliphandler.dto;

import jakarta.validation.constraints.NotNull;

public record ClipboardItemRequest(
        @NotNull String content,
        boolean file
) {}
