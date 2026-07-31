package com.saattech.service;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;


public interface OmdbService {

    ContentRequestDto fetchFromOmdb(String imdbId);
}
