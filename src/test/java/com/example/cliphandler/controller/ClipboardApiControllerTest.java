package com.example.cliphandler.controller;

import com.example.cliphandler.dto.ClipboardItemDto;
import com.example.cliphandler.service.ClipboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClipboardApiController.class)
class ClipboardApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClipboardService service;

    private ClipboardItemDto sampleDto() {
        return new ClipboardItemDto(1L, "alice", "note", "hello world", false,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void getAll_returns200WithItems() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/api/clipboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[0].name").value("note"))
                .andExpect(jsonPath("$[0].content").value("hello world"));
    }

    @Test
    void getByUsername_returns200() throws Exception {
        when(service.findByUsername("alice")).thenReturn(List.of(sampleDto()));

        mockMvc.perform(get("/api/clipboard/alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("note"));
    }

    @Test
    void getByUsernameAndName_returns200_whenFound() throws Exception {
        when(service.findByUsernameAndName("alice", "note")).thenReturn(Optional.of(sampleDto()));

        mockMvc.perform(get("/api/clipboard/alice/note"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("hello world"))
                .andExpect(jsonPath("$.file").value(false));
    }

    @Test
    void getByUsernameAndName_returns404_whenMissing() throws Exception {
        when(service.findByUsernameAndName("alice", "ghost")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/clipboard/alice/ghost"))
                .andExpect(status().isNotFound());
    }

    @Test
    void put_upserts_andReturns200() throws Exception {
        when(service.upsert(eq("alice"), eq("note"), eq("hello world"), eq(false)))
                .thenReturn(sampleDto());

        mockMvc.perform(put("/api/clipboard/alice/note")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("content", "hello world", "file", false))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.name").value("note"));
    }

    @Test
    void put_withFileFlag_setsFileTrue() throws Exception {
        ClipboardItemDto fileDto = new ClipboardItemDto(2L, "alice", "doc", "base64==", true,
                LocalDateTime.now(), LocalDateTime.now());
        when(service.upsert(eq("alice"), eq("doc"), eq("base64=="), eq(true))).thenReturn(fileDto);

        mockMvc.perform(put("/api/clipboard/alice/doc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("content", "base64==", "file", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.file").value(true));
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/clipboard/alice/note"))
                .andExpect(status().isNoContent());

        verify(service).delete("alice", "note");
    }

    @Test
    void deleteByUsername_returns204() throws Exception {
        mockMvc.perform(delete("/api/clipboard/alice"))
                .andExpect(status().isNoContent());

        verify(service).deleteByUsername("alice");
    }

    @Test
    void put_returns400_whenContentMissing() throws Exception {
        mockMvc.perform(put("/api/clipboard/alice/note")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("file", false))))
                .andExpect(status().isBadRequest());
    }
}
