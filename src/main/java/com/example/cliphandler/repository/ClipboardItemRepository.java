package com.example.cliphandler.repository;

import com.example.cliphandler.entity.ClipboardItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClipboardItemRepository extends JpaRepository<ClipboardItem, Long> {

    List<ClipboardItem> findAllByOrderByUsernameAscUpdatedAtDesc();

    List<ClipboardItem> findByUsernameOrderByUpdatedAtDesc(String username);

    Optional<ClipboardItem> findByUsernameAndName(String username, String name);

    boolean existsByUsernameAndName(String username, String name);

    @Transactional
    void deleteByUsernameAndName(String username, String name);

    @Transactional
    void deleteByUsername(String username);
}
