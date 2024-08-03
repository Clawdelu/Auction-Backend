package com.assist.Internship_2024_java_yellow.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaFilesDTO {

    private String fileUrl;

    private String fileName;

    private String mediaType;
}
