package com.saattech.service.implementation;

import com.saattech.config.ConfigValues;
import com.saattech.dto.omdb.OmdbResponseDto;
import com.saattech.dto.request.CastRequestDto;
import com.saattech.dto.request.ContentCastRequestDto;
import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.CastResponseDto;
import com.saattech.enums.CastType;
import com.saattech.exception.ResourceNotFoundException;
import com.saattech.mapper.OmdbMapper;
import com.saattech.service.CastService;
import com.saattech.service.OmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OmdbServiceImpl implements OmdbService {

    private final RestTemplate restTemplate;
    private final OmdbMapper omdbMapper;
    private final ConfigValues configValues;
    private final CastService castService;



    @Override
    public ContentRequestDto fetchFromOmdb(String imdbId) {
        String requestUrl = configValues.getApiUrl() + "?apikey=" + configValues.getApiKey() + "&i=" + imdbId;
        OmdbResponseDto responseDto = restTemplate.getForObject(requestUrl, OmdbResponseDto.class);
        ContentRequestDto requestDto = omdbMapper.toContentRequestDto(responseDto);
        if (requestDto == null) {
            throw new ResourceNotFoundException("Movie not found in OMDB for ID: " + imdbId);
        }

        List<ContentCastRequestDto> casts = new ArrayList<>();

        parseAndAddCasts(responseDto.getActors(), CastType.ACTOR, casts);
        parseAndAddCasts(responseDto.getDirector(), CastType.DIRECTOR, casts);
        parseAndAddCasts(responseDto.getWriter(), CastType.WRITER, casts);

        requestDto.setCasts(casts);

        return requestDto;
    }

    private void parseAndAddCasts(String omdbString, CastType role, List<ContentCastRequestDto> castsList) {
        if (omdbString != null && !omdbString.equalsIgnoreCase("N/A")) {

            String[] names = omdbString.split(",");

            for (String name : names) {

                CastRequestDto castReq = new CastRequestDto();
                castReq.setName(name.trim());


                CastResponseDto savedCast = castService.saveCast(castReq);

                ContentCastRequestDto ccReq = new ContentCastRequestDto();
                ccReq.setCastId(savedCast.getId());
                ccReq.setRole(role);
                ccReq.setCastName(savedCast.getName());

                castsList.add(ccReq);
            }
        }
    }

}
