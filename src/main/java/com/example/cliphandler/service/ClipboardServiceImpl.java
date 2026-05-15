package com.example.cliphandler.service;

import com.example.cliphandler.dto.ClipboardItemDto;
import com.example.cliphandler.entity.ClipboardItem;
import com.example.cliphandler.repository.ClipboardItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClipboardServiceImpl implements ClipboardService {

    private final ClipboardItemRepository repository;

    public ClipboardServiceImpl(ClipboardItemRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClipboardItemDto> findAll() {
        return repository.findAllByOrderByUsernameAscUpdatedAtDesc()
                .stream()
                .map(ClipboardItemDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClipboardItemDto> findByUsername(String username) {
        return repository.findByUsernameOrderByUpdatedAtDesc(username)
                .stream()
                .map(ClipboardItemDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClipboardItemDto> findByUsernameAndName(String username, String name) {
        return repository.findByUsernameAndName(username, name)
                .map(ClipboardItemDto::from);
    }

    @Override
    public ClipboardItemDto upsert(String username, String name, String content, boolean isFile) {
        ClipboardItem item = repository.findByUsernameAndName(username, name)
                .orElseGet(ClipboardItem::new);
        item.setUsername(username);
        item.setName(name);
        item.setContent(content);
        item.setFile(isFile);
        return ClipboardItemDto.from(repository.save(item));
    }

    @Override
    public void delete(String username, String name) {
        repository.deleteByUsernameAndName(username, name);
    }

    @Override
    public void deleteByUsername(String username) {
        repository.deleteByUsername(username);
    }
}
