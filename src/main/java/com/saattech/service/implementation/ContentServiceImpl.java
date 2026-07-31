package com.saattech.service.implementation;

import com.saattech.dto.request.ContentCastRequestDto;
import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.entity.Cast;
import com.saattech.entity.Content;
import com.saattech.entity.ContentCast;
import com.saattech.entity.Metadata;
import com.saattech.enums.CastType;
import com.saattech.enums.EntityStatus;
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
        Content content = contentRepository.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + id));

        return contentMapper.toDto(content);
    }

    @Transactional
    @Override
    public ContentResponseDto saveContent(ContentRequestDto requestDto) {

        Content parent = null;
        if (requestDto.getParentId() != null) {
            parent = contentRepository.findByIdAndStatus(requestDto.getParentId(), EntityStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent content not found! ID: " + requestDto.getParentId()));
        }
        Content content = createContentRecursively(requestDto, parent);
        Content savedContent = contentRepository.save(content);
        return contentMapper.toDto(savedContent);
    }

    @Transactional
    @Override
    public void deleteContent(Long id){
        Content content = contentRepository.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Content to delete not found! ID: " + id));
        content.setStatus(EntityStatus.DELETED);
        contentRepository.save(content);
    }

    @Transactional
    @Override
    public void addCastToContent(Long contentId, Long castId, CastType role) {
        Content content = contentRepository.findByIdAndStatus(contentId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found!"));
        Cast cast = castRepository.findById(castId)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found!"));

        boolean alreadyExists = content.getCastMembers().stream()
                .anyMatch(cc -> cc.getCast().getId().equals(castId) && cc.getRole() == role);

        if (!alreadyExists) {
            ContentCast contentCast = new ContentCast();
            contentCast.setContent(content);
            contentCast.setCast(cast);
            contentCast.setRole(role);
            content.getCastMembers().add(contentCast);
            contentRepository.save(content);
        }
    }

    @Transactional
    @Override
    public void removeCastFromContent(Long contentId, Long castId) {
        Content content = contentRepository.findByIdAndStatus(contentId, EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found!"));

        content.getCastMembers().removeIf(cc -> cc.getCast().getId().equals(castId));
        contentRepository.save(content);
    }

    private Content createContentRecursively(ContentRequestDto dto, Content parent) {
        Content content = contentMapper.toEntity(dto);
        content.setParentContent(parent);

        List<ContentCast> finalCasts = new java.util.ArrayList<>();
        if (dto.getCasts() != null && !dto.getCasts().isEmpty()) {
            for (ContentCastRequestDto castDto : dto.getCasts()) {
                Cast cast = castRepository.findById(castDto.getCastId())
                        .orElseThrow(() -> new ResourceNotFoundException("Person not found! ID: " + castDto.getCastId()));

                ContentCast contentCast = new ContentCast();
                contentCast.setContent(content);
                contentCast.setCast(cast);
                contentCast.setRole(castDto.getRole());
                finalCasts.add(contentCast);
            }
        }
        content.setCastMembers(finalCasts);

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
        Content content = contentRepository.findByIdAndStatus(id, EntityStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found! ID: " + id));

        contentMapper.updateEntityFromDto(requestDto, content);

        if (requestDto.getParentId() != null) {
            Content parent = contentRepository.findByIdAndStatus(requestDto.getParentId(), EntityStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent content not found! ID: " + requestDto.getParentId()));
            content.setParentContent(parent);
        }

        if (requestDto.getMetadata() != null) {
            if (content.getMetadata() == null) {
                Metadata metadata = metadataService.createMetadata(requestDto.getMetadata());
                content.setMetadata(metadata);
            } else {
                metadataService.updateMetadata(content.getMetadata(), requestDto.getMetadata());
            }
        }
        if (requestDto.getCasts() != null) {
            content.getCastMembers().clear();
            for (ContentCastRequestDto castDto : requestDto.getCasts()) {
                Cast cast = castRepository.findById(castDto.getCastId())
                        .orElseThrow(() -> new ResourceNotFoundException("Person not found! ID: " + castDto.getCastId()));

                ContentCast contentCast = new ContentCast();
                contentCast.setContent(content);
                contentCast.setCast(cast);
                contentCast.setRole(castDto.getRole());
                content.getCastMembers().add(contentCast);
            }
        }
        return contentMapper.toDto(content);
    }
}
