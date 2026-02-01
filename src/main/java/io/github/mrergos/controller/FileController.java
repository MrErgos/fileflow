package io.github.mrergos.controller;

import io.github.mrergos.dto.FileDownloadResponse;
import io.github.mrergos.dto.FileStatsResponse;
import io.github.mrergos.service.FileService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.UploadedFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record FileController(FileService fileService) {

    public void handleUploadFile(@NotNull Context context) {
        UploadedFile file = context.uploadedFile("newFile");
        if (file == null) {
            throw new BadRequestResponse("File not found in request\nФайл не найден в запросе");
        }

        context.json(fileService.saveFile(file, context.sessionAttribute("userId")));
    }

    public void handleDownloadFile(@NotNull Context context) {
        String id = context.pathParam("id");
        FileDownloadResponse file = fileService.getFileForDownload(id);

        context.contentType(file.contentType());
        context.header("Content-Disposition", "attachment; filename=\"" + file.filename() + "\"");
        context.result(file.inputStream());
    }

    public void handleStats(@NotNull Context context) {
        Long userId = context.sessionAttribute("userId");
        if (userId == null) throw new UnauthorizedResponse();

        List<FileStatsResponse> stats = fileService.getUserFiles(userId);
        context.json(stats);
    }
}
