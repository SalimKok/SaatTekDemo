package com.saattech.mapper;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.entity.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContentMapper {

    private final CastMapper castMapper;
    private final MetadataMapper metadataMapper;

    public ContentResponseDto toDto(Content content){
        if (content == null) {
            return null;
        }

        ContentResponseDto dto = new ContentResponseDto();
        dto.setId(content.getId());
        dto.setTitle(content.getTitle());
        dto.setSeasonNo(content.getSeasonNo());
        dto.setEpisodeNo(content.getEpisodeNo());
        dto.setPoster(content.getPoster());
        dto.setContentType(content.getContentType());
        dto.setCreatedAt(content.getCreatedAt());

        if (content.getSubContents() != null && !content.getSubContents().isEmpty()){
            List<ContentResponseDto> subDtoList = content.getSubContents().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());

            dto.setSubContents(subDtoList);
        }

        if (content.getCasts() != null && !content.getCasts().isEmpty()) {
            dto.setCasts(content.getCasts().stream()
                    .map(castMapper::toDto)
                    .collect(Collectors.toList()));
        }

        if (content.getMetadata() != null) {
            dto.setMetadata(metadataMapper.toDto(content.getMetadata()));
        }

        return dto;
    }

    public Content toEntity(ContentRequestDto requestDto){
        if (requestDto == null) {
            return null;
        }

        Content content = new Content();
        content.setTitle(requestDto.getTitle());
        content.setSeasonNo(requestDto.getSeasonNo());
        content.setEpisodeNo(requestDto.getEpisodeNo());
        content.setPoster(requestDto.getPoster());
        content.setContentType(requestDto.getContentType());

        return content;
    }

    public void updateEntityFromDto(ContentRequestDto dto, Content content) {
        if (dto == null || content == null) return;

        if (dto.getTitle() != null) content.setTitle(dto.getTitle());
        if (dto.getSeasonNo() != null) content.setSeasonNo(dto.getSeasonNo());
        if (dto.getEpisodeNo() != null) content.setEpisodeNo(dto.getEpisodeNo());
        if (dto.getPoster() != null) content.setPoster(dto.getPoster());
        if (dto.getContentType() != null) content.setContentType(dto.getContentType());

    }
}
