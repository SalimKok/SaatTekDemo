package com.saattech.service.implementation;

import com.saattech.dto.request.CastRequestDto;
import com.saattech.dto.response.CastResponseDto;
import com.saattech.entity.Cast;
import com.saattech.enums.EntityStatus;
import com.saattech.mapper.CastMapper;
import com.saattech.repository.CastRepository;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.service.CastService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CastServiceImpl implements CastService {

    private final CastRepository castRepository;
    private final CastMapper castMapper;

    @Override
    public List<CastResponseDto> getAllCasts() {
        List<Cast> casts = castRepository.findByStatus(EntityStatus.ACTIVE);

        return casts.stream()
                .map(castMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CastResponseDto saveActor(CastRequestDto requestDto) {
        Cast cast = castMapper.toEntity(requestDto);
        Cast savedCast = castRepository.save(cast);
        return castMapper.toDto(savedCast);
    }

    @Override
    public void deleteCast(Long id) {
        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No cast found to delete! ID: " + id));

        cast.setStatus(EntityStatus.DELETED);
        castRepository.save(cast);
    }

    @Transactional
    @Override
    public CastResponseDto updateCast(Long id, CastRequestDto requestDto) {
        Cast cast = castRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cast not found! ID: " + id));

        castMapper.updateEntityFromDto(requestDto, cast);

        return castMapper.toDto(cast);
    }
}
