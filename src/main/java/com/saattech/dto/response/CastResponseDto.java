package com.saattech.dto.response;

import com.saattech.enums.CastType;
import lombok.Data;

@Data
public class CastResponseDto {

    private Long id;
    private String name;
    private CastType type;
}
