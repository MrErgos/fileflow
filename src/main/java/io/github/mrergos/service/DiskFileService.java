package io.github.mrergos.service;

import io.github.mrergos.dao.FileDao;
import io.github.mrergos.dto.FileDownloadResponse;
import io.github.mrergos.dto.FileResponse;
import io.github.mrergos.entity.FileRecord;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DiskFileService implements FileService{
    private static final Logger log = LoggerFactory.getLogger(DiskFileService.class);
    private final FileDao fileDao;
    private final Path rootPath;
    private final String baseUrl;
    private final int cleanupDays;

    public DiskFileService(FileDao fileDao, Properties properties) {
        this.fileDao = fileDao;
        baseUrl = properties.getProperty("server.url.download", "http://localhost:8080/download/");
        rootPath = Paths.get(properties.getProperty("storage.root_path", "uploads")).toAbsolutePath().normalize();
        cleanupDays = Integer.parseInt(properties.getProperty("storage.cleanup_days", "30"));

        if (Files.notExists(rootPath)) {
            try {
                Files.createDirectories(rootPath);
            } catch (IOException e) {
                log.error("Could not create root upload directory");
                throw new RuntimeException(e);
            }
        }
    }

    public String generateFilename() {
        return UUID.randomUUID().toString();
    }

    @Override
    public FileResponse saveFile(UploadedFile uploadedFile) {
        String id = generateFilename();

        try {
            Files.copy(uploadedFile.content(), rootPath.resolve(id));
        } catch (IOException e) {
            log.error("Failed to save the file: {}", uploadedFile.filename());
            throw new InternalServerErrorResponse("Failed to save the file");
        }
        String contentType = uploadedFile.contentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }
        fileDao.insertFile(id, uploadedFile.filename(), id, contentType, uploadedFile.size());
        return new FileResponse(baseUrl + id, uploadedFile.size());
    }

    @Override
    public FileDownloadResponse getFileForDownload(String id) {
        FileRecord file = fileDao.findFile(id);
        if (file == null) {
            throw new NotFoundResponse("There is no such file like " + id);
        }
        fileDao.updateFileMeta(id);

        Path filePath = rootPath.resolve(file.getPath()).normalize();

        if (!filePath.startsWith(rootPath)) {
            throw new InternalServerErrorResponse("Path traversal attempt detected");
        }

        try {
            InputStream is = Files.newInputStream(filePath);
            String contentType = file.getContentType();
            return new FileDownloadResponse(is, file.getOriginalName(), contentType);
        } catch (IOException e) {
            log.error("Failed to read the file: {}", file.getId());
            throw new InternalServerErrorResponse("Error reading file");
        }
    }

    @Override
    public void deleteExpiredFiles() {
        List<FileRecord> expiredFiles = fileDao.findExpiredFiles("-" + cleanupDays + " days");

        if (expiredFiles.isEmpty()) {
            return;
        }

        List<String> deletedFiles = new LinkedList<>();
        expiredFiles
                .forEach(fileRecord -> {
                    try {
                        Files.deleteIfExists(rootPath.resolve(fileRecord.getPath()));
                        deletedFiles.add(fileRecord.getId());
                    } catch (IOException e) {
                        log.error("File {} was not deleted", fileRecord.getId());
                    }
                });

        fileDao.deleteFiles(deletedFiles);

        log.info("Cleanup finished. Deleted {} records", deletedFiles.size());
    }

}
