package com.assist.Internship_2024_java_yellow.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "media_files")
public class MediaFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "media_type")
    private String mediaType;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "auction_id")
    private Auction auction;
}
