package io.github.mrergos.mapper;

import io.github.mrergos.dto.FileStatsResponse;
import io.github.mrergos.entity.FileRecord;

public class FileRecordMapper {
    public static FileStatsResponse toFileStatsResponse(FileRecord fileRecord, String baseUrl) {
        return new FileStatsResponse(baseUrl + fileRecord.getId(),
                fileRecord.getOriginalName(),
                fileRecord.getContentType(),
                fileRecord.getFileSize(),
                fileRecord.getDownloadCount(),
                fileRecord.getUploadDate(),
                fileRecord.getLastAccessedAt());
    }
}
