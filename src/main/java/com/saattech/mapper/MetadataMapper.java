package com.saattech.mapper;

import com.saattech.dto.request.MetadataRequestDto;
import com.saattech.dto.response.MetadataResponseDto;
import com.saattech.entity.Metadata;
import org.springframework.stereotype.Component;

@Component
public class MetadataMapper {
    public MetadataResponseDto toDto(Metadata metadata) {
        if (metadata == null) return null;

        MetadataResponseDto dto = new MetadataResponseDto();

        dto.setId(metadata.getId());
        dto.setPlot(metadata.getPlot());
        dto.setImdbRating(metadata.getImdbRating());
        dto.setGenre(metadata.getGenre());
        dto.setLanguage(metadata.getLanguage());
        dto.setCountry(metadata.getCountry());
        dto.setReleased(metadata.getReleased());
        dto.setRuntime(metadata.getRuntime());
        dto.setImdbVotes(metadata.getImdbVotes());
        return dto;
    }
    public Metadata toEntity(MetadataRequestDto requestDto) {
        if (requestDto == null) return null;

        Metadata metadata = new Metadata();

        metadata.setPlot(requestDto.getPlot());
        metadata.setImdbRating(requestDto.getImdbRating());
        metadata.setGenre(requestDto.getGenre());
        metadata.setLanguage(requestDto.getLanguage());
        metadata.setCountry(requestDto.getCountry());
        metadata.setReleased(requestDto.getReleased());
        metadata.setRuntime(requestDto.getRuntime());
        metadata.setImdbVotes(requestDto.getImdbVotes());
        return metadata;
    }

    public void updateEntityFromDto(MetadataRequestDto dto, Metadata metadata) {
        if (dto == null || metadata == null) return;

        if (dto.getPlot() != null) metadata.setPlot(dto.getPlot());
        if (dto.getImdbRating() != null) metadata.setImdbRating(dto.getImdbRating());
        if (dto.getGenre() != null) metadata.setGenre(dto.getGenre());
        if (dto.getLanguage() != null) metadata.setLanguage(dto.getLanguage());
        if (dto.getCountry() != null) metadata.setCountry(dto.getCountry());
        if (dto.getReleased() != null) metadata.setReleased(dto.getReleased());
        if (dto.getRuntime() != null) metadata.setRuntime(dto.getRuntime());
        if (dto.getImdbVotes() != null) metadata.setImdbVotes(dto.getImdbVotes());
    }
}
