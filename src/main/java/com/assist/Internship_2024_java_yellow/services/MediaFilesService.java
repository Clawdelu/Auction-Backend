package com.assist.Internship_2024_java_yellow.services;

import com.assist.Internship_2024_java_yellow.dtos.MediaFilesDTO;
import com.assist.Internship_2024_java_yellow.entities.Auction;
import com.assist.Internship_2024_java_yellow.entities.MediaFiles;

public interface MediaFilesService {

    MediaFilesDTO addMediaFile(MediaFiles mediaFile);

    void deleteMediaFile(String mediaFileName);

    String createFileName(String auctionIdentifier, String fileName);
}
