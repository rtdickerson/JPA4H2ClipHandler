package com.example.cliphandler.service;

import com.example.cliphandler.dto.ClipboardItemDto;
import com.example.cliphandler.entity.ClipboardItem;
import com.example.cliphandler.repository.ClipboardItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClipboardServiceTest {

    @Mock
    private ClipboardItemRepository repository;

    @InjectMocks
    private ClipboardServiceImpl service;

    private ClipboardItem aliceNote;

    @BeforeEach
    void setUp() {
        aliceNote = new ClipboardItem();
        aliceNote.setId(1L);
        aliceNote.setUsername("alice");
        aliceNote.setName("note");
        aliceNote.setContent("hello world");
        aliceNote.setFile(false);
        aliceNote.setCreatedAt(LocalDateTime.now());
        aliceNote.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void findAll_mapsToDto() {
        when(repository.findAllByOrderByUsernameAscUpdatedAtDesc()).thenReturn(List.of(aliceNote));

        List<ClipboardItemDto> result = service.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).username()).isEqualTo("alice");
        assertThat(result.get(0).name()).isEqualTo("note");
    }

    @Test
    void findByUsername_filtersCorrectly() {
        when(repository.findByUsernameOrderByUpdatedAtDesc("alice")).thenReturn(List.of(aliceNote));

        List<ClipboardItemDto> result = service.findByUsername("alice");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).content()).isEqualTo("hello world");
    }

    @Test
    void findByUsernameAndName_returnsDto_whenFound() {
        when(repository.findByUsernameAndName("alice", "note")).thenReturn(Optional.of(aliceNote));

        Optional<ClipboardItemDto> result = service.findByUsernameAndName("alice", "note");

        assertThat(result).isPresent();
        assertThat(result.get().content()).isEqualTo("hello world");
    }

    @Test
    void findByUsernameAndName_returnsEmpty_whenMissing() {
        when(repository.findByUsernameAndName("alice", "ghost")).thenReturn(Optional.empty());

        Optional<ClipboardItemDto> result = service.findByUsernameAndName("alice", "ghost");

        assertThat(result).isEmpty();
    }

    @Test
    void upsert_createsNewItem_whenNotExists() {
        when(repository.findByUsernameAndName("alice", "new")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(aliceNote);

        ClipboardItemDto result = service.upsert("alice", "new", "content", false);

        assertThat(result).isNotNull();
        verify(repository).save(any(ClipboardItem.class));
    }

    @Test
    void upsert_updatesExistingItem() {
        aliceNote.setContent("updated");
        when(repository.findByUsernameAndName("alice", "note")).thenReturn(Optional.of(aliceNote));
        when(repository.save(aliceNote)).thenReturn(aliceNote);

        ClipboardItemDto result = service.upsert("alice", "note", "updated", false);

        assertThat(result.content()).isEqualTo("updated");
        verify(repository).save(aliceNote);
    }

    @Test
    void upsert_setsFileFlag() {
        ClipboardItem fileItem = new ClipboardItem();
        fileItem.setId(2L);
        fileItem.setUsername("alice");
        fileItem.setName("doc");
        fileItem.setContent("base64==");
        fileItem.setFile(true);
        fileItem.setCreatedAt(LocalDateTime.now());
        fileItem.setUpdatedAt(LocalDateTime.now());

        when(repository.findByUsernameAndName("alice", "doc")).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(fileItem);

        ClipboardItemDto result = service.upsert("alice", "doc", "base64==", true);

        assertThat(result.file()).isTrue();
    }

    @Test
    void delete_delegatesToRepository() {
        service.delete("alice", "note");

        verify(repository).deleteByUsernameAndName("alice", "note");
    }

    @Test
    void deleteByUsername_delegatesToRepository() {
        service.deleteByUsername("alice");

        verify(repository).deleteByUsername("alice");
    }
}
