package com.assist.Internship_2024_java_yellow.repository;

import com.assist.Internship_2024_java_yellow.entities.MediaFiles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaFilesRepository extends JpaRepository<MediaFiles, Integer> {

    Optional<MediaFiles> findByFileName(String fileName);

    boolean existsByFileName(String fileName);
}
