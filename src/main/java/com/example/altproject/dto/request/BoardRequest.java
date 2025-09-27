package com.example.altproject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Set<String> hashtags;

    @NotNull
    private List<String> boardImageList;
}
