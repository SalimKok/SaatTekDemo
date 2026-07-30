package com.saattech.dto.request;

import com.saattech.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ContentRequestDto {

    @NotBlank(message = "Content title cannot be left blank.")
    private String title;

    private Integer seasonNo;
    private Integer episodeNo;
    private String poster;

    @NotNull(message = "Content type cannot be left blank.")
    private ContentType contentType;

    private Long parentId;
    private List<Long> castIds;
    private List<ContentRequestDto> subContents;
    private MetadataRequestDto metadata;
}