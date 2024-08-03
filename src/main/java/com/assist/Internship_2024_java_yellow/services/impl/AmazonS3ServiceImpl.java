package com.assist.Internship_2024_java_yellow.services.impl;

import com.amazonaws.services.s3.model.*;
import com.assist.Internship_2024_java_yellow.services.AuctionService;
import com.assist.Internship_2024_java_yellow.services.AmazonS3Service;
import com.assist.Internship_2024_java_yellow.services.SMTPGmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    @Value("${aws.bucketName}")
    private String AWSBucketName;

    @Value("${aws.endpoint_url}")
    private String AWSEndpointUrl;

    private final AmazonS3 amazonS3;

    public String uploadFileToAmazonS3(MultipartFile file, String fileName) throws SdkClientException, IOException {

        File temporaryFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);

        file.transferTo(temporaryFile);

        amazonS3.putObject(new PutObjectRequest(AWSBucketName, fileName, temporaryFile).withCannedAcl(CannedAccessControlList.PublicRead));

        return String.format("%s/%s", AWSEndpointUrl, fileName);
    }

    public void deleteFileFromAmazonS3(String fileName) {

        amazonS3.deleteObject(AWSBucketName, fileName);
    }

}