package org.example.assetmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetResponse {
    private Long id;
    private String assetCode;
    private String name;
    private String description;
    private String category;
    private String status;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}