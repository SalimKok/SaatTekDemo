package com.saattech.service.implementation;

import com.saattech.dto.request.MetadataRequestDto;
import com.saattech.entity.Metadata;
import com.saattech.mapper.MetadataMapper;
import com.saattech.repository.MetadataRepository;
import com.saattech.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetadataServiceImpl implements MetadataService {

    private final MetadataRepository metadataRepository;
    private final MetadataMapper metadataMapper;

    @Override
    public Metadata createMetadata(MetadataRequestDto requestDto) {
        if (requestDto.getImdbID() != null && metadataRepository.existsByImdbID(requestDto.getImdbID())) {
            throw new RuntimeException("This movie already exists in the database! IMDB ID: " + requestDto.getImdbID());
        }

        Metadata metadata = metadataMapper.toEntity(requestDto);
        return metadataRepository.save(metadata);
    }
    @Override
    public void updateMetadata(Metadata metadata, MetadataRequestDto requestDto) {

        if (requestDto.getImdbID() != null
            && !requestDto.getImdbID().equals(metadata.getImdbID())
            && metadataRepository.existsByImdbID(requestDto.getImdbID())) {
        throw new RuntimeException("This IMDB ID is already in use by another movie!");
    }

        metadataMapper.updateEntityFromDto(requestDto, metadata);
        metadataRepository.save(metadata);
    }
}
