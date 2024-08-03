package com.assist.Internship_2024_java_yellow.mappers;

import com.assist.Internship_2024_java_yellow.dtos.MediaFilesDTO;
import com.assist.Internship_2024_java_yellow.entities.MediaFiles;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MediaFilesMapper {

    MediaFilesMapper INSTANCE = Mappers.getMapper(MediaFilesMapper.class);

    public MediaFiles toMediaFilesFromMediaFilesDTO(MediaFilesDTO mediaFilesDTO);

    public MediaFilesDTO toMediaFilesDTOFromMediaFiles(MediaFiles mediaFiles);

    List<MediaFilesDTO> toMediaFilesDto(List<MediaFiles> mediaFilesList);
}
