package com.saattech.dto.request;

import com.saattech.enums.CastType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CastRequestDto {

    @NotBlank(message = "Cast name cannot be left blank.")
    private String name;

    @NotNull(message = "Cast type cannot be left blank.")
    private CastType type;
}

