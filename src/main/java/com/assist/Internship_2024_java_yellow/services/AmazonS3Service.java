package com.assist.Internship_2024_java_yellow.services;

import com.amazonaws.SdkClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface AmazonS3Service {

    String uploadFileToAmazonS3(MultipartFile file, String fileName) throws SdkClientException, IOException;

    void deleteFileFromAmazonS3(String fileName);

}