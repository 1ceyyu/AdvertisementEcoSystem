package com.example.videowebplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdVideo {
    private int id;
    private String title;
    private String fileName;
    private long durationSeconds;
    private long fileLengthBytes;
}
