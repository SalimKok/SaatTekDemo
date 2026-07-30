package com.saattech.mapper;

import com.saattech.dto.request.CastRequestDto;
import com.saattech.dto.response.CastResponseDto;
import com.saattech.entity.Cast;
import org.springframework.stereotype.Component;

@Component
public class CastMapper {

    public CastResponseDto toDto(Cast cast){
        if(cast == null){
            return  null;
        }

        CastResponseDto dto = new CastResponseDto();
        dto.setId(cast.getId());
        dto.setName(cast.getName());
        dto.setType(cast.getType());

        return dto;
    }

    public Cast toEntity(CastRequestDto requestDto){
        if (requestDto == null){
            return null;
        }

        Cast cast = new Cast();
        cast.setName(requestDto.getName());
        cast.setType(requestDto.getType());

        return cast;
    }

    public void updateEntityFromDto(CastRequestDto dto, Cast cast) {
        if (dto == null || cast == null) return;

        if (dto.getName() != null) cast.setName(dto.getName());
        if (dto.getType() != null) cast.setType(dto.getType());
    }
}
