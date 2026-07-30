package com.saattech.service.implementation;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.entity.Cast;
import com.saattech.entity.Content;
import com.saattech.entity.Metadata;
import com.saattech.enums.EntityStatus;
import com.saattech.mapper.CastMapper;
import com.saattech.mapper.ContentMapper;
import com.saattech.repository.CastRepository;
import com.saattech.repository.ContentRepository;
import com.saattech.saattech.exception.ResourceNotFoundException;
import com.saattech.service.ContentService;
import com.saattech.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;
    private final CastRepository castRepository;
    private final MetadataService metadataService;

    @Override
    public Page<ContentResponseDto> getAllContents(Pageable pageable) {
        Page<Content> contentPage = contentRepository.findByStatus(EntityStatus.ACTIVE, pageable);

        return contentPage.map(contentMapper::toDto);
    }

    @Override
    public ContentResponseDto getContentById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + id));

        return contentMapper.toDto(content);
    }

    @Override
    public ContentResponseDto saveContent(ContentRequestDto requestDto) {

        Content parent = null;
        if (requestDto.getParentId() != null) {
            parent = contentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent content not found! ID: " + requestDto.getParentId()));
        }

        Content content = createContentRecursively(requestDto, parent);

        Content savedContent = contentRepository.save(content);
        return contentMapper.toDto(savedContent);
    }

    @Override
    public void deleteContent(Long id){
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No content found to delete! ID: " + id));

        content.setStatus(EntityStatus.DELETED);
        contentRepository.save(content);
    }

    @Override
    public void addCastToContent(Long contentId, Long castId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found!"));
        Cast cast = castRepository.findById(castId)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found!"));

        if (!content.getCasts().contains(cast)) {
            content.getCasts().add(cast);
            contentRepository.save(content);
        }
    }

    @Override
    public void removeCastFromContent(Long contentId, Long castId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found!"));
        Cast cast = castRepository.findById(castId)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found!"));

        if (content.getCasts().contains(cast)) {
            content.getCasts().remove(cast);
            contentRepository.save(content);
        }
    }

    private Content createContentRecursively(ContentRequestDto dto, Content parent) {
        Content content = contentMapper.toEntity(dto);

        content.setParentContent(parent);

        List<Cast> finalCasts = new java.util.ArrayList<>();
        if (dto.getCastIds() != null && !dto.getCastIds().isEmpty()) {
            List<Cast> existingCasts = castRepository.findAllById(dto.getCastIds());
            finalCasts.addAll(existingCasts);
        }

        content.setCasts(finalCasts);

        if (dto.getMetadata() != null) {
            Metadata metadata = metadataService.createMetadata(dto.getMetadata());
            content.setMetadata(metadata);
        }
        if (dto.getSubContents() != null && !dto.getSubContents().isEmpty()) {
            List<Content> children = dto.getSubContents().stream()
                    .map(subDto -> createContentRecursively(subDto, content))
                    .collect(Collectors.toList());
            content.setSubContents(children);
        }

        return content;
    }

    @Transactional
    @Override
    public ContentResponseDto updateContent(Long id, ContentRequestDto requestDto) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + id));
        contentMapper.updateEntityFromDto(requestDto, content);
        if (requestDto.getParentId() != null) {
            Content parent = contentRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent content not found! ID: " + requestDto.getParentId()));
            content.setParentContent(parent);
        }
        // Metadata güncelleme artık MetadataService üzerinden
        if (requestDto.getMetadata() != null) {
            if (content.getMetadata() == null) {
                Metadata metadata = metadataService.createMetadata(requestDto.getMetadata());
                content.setMetadata(metadata);
            } else {
                metadataService.updateMetadata(content.getMetadata(), requestDto.getMetadata());
            }
        }
        if (requestDto.getCastIds() != null) {
            List<Cast> updatedCasts = requestDto.getCastIds().stream()
                    .map(castId -> castRepository.getReferenceById(castId))
                    .collect(Collectors.toList());

            content.setCasts(updatedCasts);
        }
        return contentMapper.toDto(content);
    }
}
