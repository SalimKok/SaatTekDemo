package com.saattech.service;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContentService {

    Page<ContentResponseDto> getAllContents(Pageable pageable);

    ContentResponseDto saveContent(ContentRequestDto requestDto);

    void deleteContent(Long id);

    void addCastToContent(Long contentId, Long castId);

    void removeCastFromContent(Long contentId, Long castId);

    ContentResponseDto updateContent(Long id, ContentRequestDto requestDto);

    ContentResponseDto getContentById(Long id);

}
