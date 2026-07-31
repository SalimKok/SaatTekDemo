package com.saattech.controller;

import com.saattech.dto.request.ContentRequestDto;
import com.saattech.dto.response.ContentResponseDto;
import com.saattech.service.OmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/omdb")
@RequiredArgsConstructor
public class OmdbController {
    private final OmdbService omdbService;


    @GetMapping("/preview")
    public ResponseEntity<ContentRequestDto> previewFromOmdb(@RequestParam String imdbId) {
        ContentRequestDto requestDto = omdbService.fetchFromOmdb(imdbId);
        return new ResponseEntity<>(requestDto, HttpStatus.OK);
    }
}
