package io.github.mrergos.controller;

import io.github.mrergos.dto.FileDownloadResponse;
import io.github.mrergos.service.FileService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import org.jetbrains.annotations.NotNull;

public record FileController(FileService fileService) {

    public void handleUploadFile(@NotNull Context context) {
        UploadedFile file = context.uploadedFile("newFile");
        if (file == null) {
            throw new BadRequestResponse("File not found in request");
        }

        context.json(fileService.saveFile(file));
    }

    public void handleDownloadFile(@NotNull Context context) {
        String id = context.pathParam("id");
        FileDownloadResponse file = fileService.getFileForDownload(id);

        context.contentType(file.contentType());
        context.header("Content-Disposition", "attachment; filename=\"" + file.filename() + "\"");
        context.result(file.inputStream());
    }
}
