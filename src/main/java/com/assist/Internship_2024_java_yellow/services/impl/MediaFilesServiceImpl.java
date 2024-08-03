package com.assist.Internship_2024_java_yellow.services.impl;

import com.assist.Internship_2024_java_yellow.dtos.MediaFilesDTO;
import com.assist.Internship_2024_java_yellow.entities.Auction;
import com.assist.Internship_2024_java_yellow.entities.MediaFiles;
import com.assist.Internship_2024_java_yellow.mappers.MediaFilesMapper;
import com.assist.Internship_2024_java_yellow.repository.MediaFilesRepository;
import com.assist.Internship_2024_java_yellow.services.MediaFilesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MediaFilesServiceImpl implements MediaFilesService {

    private final MediaFilesRepository mediaFilesRepository;

    private final MediaFilesMapper mediaFilesMapper;

    @Override
    public MediaFilesDTO addMediaFile(MediaFiles mediaFile) {

        MediaFiles savedMediaFile = mediaFilesRepository.save(mediaFile);

        return mediaFilesMapper.toMediaFilesDTOFromMediaFiles(savedMediaFile);
    }

    @Override
    public void deleteMediaFile(String mediaFileName)
    {
        MediaFiles mediaFile = mediaFilesRepository.findByFileName(mediaFileName).get();

        if (mediaFilesRepository.findByFileName(mediaFileName).isPresent()) {

            mediaFilesRepository.deleteById(mediaFile.getId());

            System.out.println("Deleted media file: " + mediaFileName);
        }
    }

    @Override
    public String createFileName(String auctionIdentifier, String originalFileName) {

        return String.format("%s-%s-%s", auctionIdentifier, System.currentTimeMillis(), originalFileName);
    }
}
