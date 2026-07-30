package com.saattech.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MetadataResponseDto {
    private Long id;
    private LocalDate released;
    private Double imdbRating;
    private String imdbVotes;
    private String runtime;
    private String genre;
    private String plot;
    private String language;
    private String country;
}
