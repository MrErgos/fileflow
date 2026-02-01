package io.github.mrergos.service;

import io.github.mrergos.dto.FileDownloadResponse;
import io.github.mrergos.dto.FileResponse;
import io.github.mrergos.dto.FileStatsResponse;
import io.github.mrergos.entity.FileRecord;
import io.javalin.http.UploadedFile;

import java.util.List;

public interface FileService {
    FileResponse saveFile(UploadedFile uploadedFile, Long userId);

    FileDownloadResponse getFileForDownload(String id);

    void deleteExpiredFiles();

    List<FileStatsResponse> getUserFiles(Long userId);
}
