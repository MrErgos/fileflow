package io.github.mrergos.dto;

public record FileStatsResponse(
        String url,
        String originalName,
        String contentType,
        Long fileSize,
        Integer downloadCount,
        String uploadDate) {
}
