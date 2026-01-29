package io.github.mrergos.service;

import io.github.mrergos.dto.FileDownloadResponse;
import io.github.mrergos.dto.FileResponse;
import io.javalin.http.UploadedFile;

public interface FileService {
    FileResponse saveFile(UploadedFile uploadedFile);

    FileDownloadResponse getFileForDownload(String id);

    void deleteExpiredFiles();
}
