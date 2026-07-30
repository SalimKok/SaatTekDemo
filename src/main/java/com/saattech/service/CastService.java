package com.saattech.service;

import com.saattech.dto.request.CastRequestDto;
import com.saattech.dto.response.CastResponseDto;
import java.util.List;

public interface CastService {

    List<CastResponseDto> getAllCasts();

    CastResponseDto saveActor(CastRequestDto requestDto);

    void deleteCast(Long id);

    CastResponseDto updateCast(Long id, CastRequestDto requestDto);
}
