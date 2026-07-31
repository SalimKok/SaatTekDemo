package com.saattech.repository;

import com.saattech.entity.Content;
import com.saattech.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    Page<Content> findByStatus(EntityStatus status, Pageable pageable);

    Optional<Content> findByIdAndStatus(Long id, EntityStatus status);
}
