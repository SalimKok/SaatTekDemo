package com.saattech.repository;

import com.saattech.entity.Content;
import com.saattech.enums.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByParentContentIsNull();
    Page<Content> findByStatus(EntityStatus status, Pageable pageable);
}
