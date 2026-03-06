package com.malgn.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContentsRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;
}
