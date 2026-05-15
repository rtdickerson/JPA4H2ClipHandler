package com.example.cliphandler.repository;

import com.example.cliphandler.entity.ClipboardItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClipboardItemRepositoryTest {

    @Autowired
    private ClipboardItemRepository repository;

    // --- save & find ---

    @Test
    void savesAndRetrievesItem() {
        repository.save(item("alice", "greeting", "hello", false));

        Optional<ClipboardItem> found = repository.findByUsernameAndName("alice", "greeting");

        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("hello");
        assertThat(found.get().isFile()).isFalse();
    }

    @Test
    void setsTimestampsOnPersist() {
        repository.save(item("alice", "ts", "value", false));

        ClipboardItem found = repository.findByUsernameAndName("alice", "ts").orElseThrow();

        assertThat(found.getCreatedAt()).isNotNull();
        assertThat(found.getUpdatedAt()).isNotNull();
    }

    @Test
    void storesFileFlagCorrectly() {
        repository.save(item("alice", "binary", "base64==", true));

        ClipboardItem found = repository.findByUsernameAndName("alice", "binary").orElseThrow();

        assertThat(found.isFile()).isTrue();
        assertThat(found.getContent()).isEqualTo("base64==");
    }

    // --- find by username ---

    @Test
    void findsAllItemsByUsername() {
        repository.save(item("alice", "a", "content-a", false));
        repository.save(item("alice", "b", "content-b", false));
        repository.save(item("bob",   "x", "content-x", false));

        List<ClipboardItem> aliceItems = repository.findByUsernameOrderByUpdatedAtDesc("alice");

        assertThat(aliceItems).hasSize(2);
        assertThat(aliceItems).extracting(ClipboardItem::getUsername).containsOnly("alice");
    }

    @Test
    void returnsEmptyList_whenUsernameHasNoItems() {
        List<ClipboardItem> result = repository.findByUsernameOrderByUpdatedAtDesc("nobody");
        assertThat(result).isEmpty();
    }

    // --- upsert via find+save ---

    @Test
    void updatesExistingItem() {
        repository.save(item("alice", "note", "original", false));

        ClipboardItem existing = repository.findByUsernameAndName("alice", "note").orElseThrow();
        existing.setContent("updated");
        repository.save(existing);

        Optional<ClipboardItem> found = repository.findByUsernameAndName("alice", "note");
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("updated");
        assertThat(repository.findByUsernameOrderByUpdatedAtDesc("alice")).hasSize(1);
    }

    // --- delete ---

    @Test
    void deletesByUsernameAndName() {
        repository.save(item("alice", "temp", "data", false));

        repository.deleteByUsernameAndName("alice", "temp");

        assertThat(repository.findByUsernameAndName("alice", "temp")).isEmpty();
    }

    @Test
    void deletesByUsername_removesOnlyThatUser() {
        repository.save(item("alice", "a", "x", false));
        repository.save(item("alice", "b", "y", false));
        repository.save(item("bob",   "c", "z", false));

        repository.deleteByUsername("alice");

        assertThat(repository.findByUsernameOrderByUpdatedAtDesc("alice")).isEmpty();
        assertThat(repository.findByUsernameOrderByUpdatedAtDesc("bob")).hasSize(1);
    }

    // --- helpers ---

    private ClipboardItem item(String username, String name, String content, boolean isFile) {
        ClipboardItem item = new ClipboardItem();
        item.setUsername(username);
        item.setName(name);
        item.setContent(content);
        item.setFile(isFile);
        return item;
    }
}
