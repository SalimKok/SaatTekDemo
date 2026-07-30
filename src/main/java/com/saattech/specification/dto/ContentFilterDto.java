package com.saattech.specification.dto;

import com.saattech.enums.ContentType;
import com.saattech.enums.EntityStatus;
import lombok.Data;

@Data
public class ContentFilterDto {
    private String title;
    private ContentType contentType;
    private EntityStatus status;
}