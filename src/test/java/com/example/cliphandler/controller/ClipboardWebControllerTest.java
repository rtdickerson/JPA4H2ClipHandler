package com.example.cliphandler.controller;

import com.example.cliphandler.service.ClipboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClipboardWebController.class)
class ClipboardWebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClipboardService service;

    @Test
    void index_renders_allItems_whenNoFilter() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("items", "filterUser"));
    }

    @Test
    void index_renders_filteredItems_whenUsernameParam() throws Exception {
        when(service.findByUsername("alice")).thenReturn(List.of());

        mockMvc.perform(get("/").param("username", "alice"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("filterUser", "alice"));
    }

    @Test
    void add_upsertsItem_andRedirects() throws Exception {
        mockMvc.perform(post("/add")
                        .param("username", "alice")
                        .param("name", "note")
                        .param("content", "hello world"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(service).upsert("alice", "note", "hello world", false);
    }

    @Test
    void add_redirectsToFilteredView_whenFilterUserSet() throws Exception {
        mockMvc.perform(post("/add")
                        .param("username", "alice")
                        .param("name", "note")
                        .param("content", "hello")
                        .param("filterUser", "alice"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?username=alice"));
    }

    @Test
    void clear_deletesItem_andRedirects() throws Exception {
        mockMvc.perform(post("/clear/alice/note"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(service).delete("alice", "note");
    }
}
