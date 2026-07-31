package com.saattech.service.implementation;

import com.saattech.config.ConfigValues;
import com.saattech.dto.omdb.OmdbResponseDto;
import com.saattech.dto.request.ContentRequestDto;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.mapper.OmdbMapper;
import com.saattech.service.OmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OmdbServiceImpl implements OmdbService {

    private final RestTemplate restTemplate;
    private final OmdbMapper omdbMapper;
    private final ConfigValues configValues;



    @Override
    public ContentRequestDto fetchFromOmdb(String imdbId) {
        String requestUrl = configValues.getApiUrl() + "?apikey=" + configValues.getApiKey() + "&i=" + imdbId;
        OmdbResponseDto responseDto = restTemplate.getForObject(requestUrl, OmdbResponseDto.class);
        ContentRequestDto requestDto = omdbMapper.toContentRequestDto(responseDto);
        if (requestDto == null) {
            throw new ResourceNotFoundException("Movie not found in OMDB for ID: " + imdbId);
        }
        return requestDto;
    }
}
