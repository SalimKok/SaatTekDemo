package com.saattech.dto.request;

import com.saattech.enums.CastType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CastRequestDto {

    @NotBlank(message = "Cast adı boş bırakılamaz")
    private String name;

    @NotNull(message = "Cast tipi boş bırakılamaz")
    private CastType type;
}

