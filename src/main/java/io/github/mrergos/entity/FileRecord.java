package io.github.mrergos.entity;

import java.util.Objects;

public final class FileRecord {
    private String id;
    private String originalName;
    private String path;
    private String contentType;
    private Long fileSize;
    private Integer downloadCount;
    private Long userId;
    private String uploadDate;
    private String lastAccessedAt;

    public FileRecord() {
    }

    public FileRecord(String id, String originalName, String path, String contentType, Long fileSize, Integer downloadCount, Long userId, String uploadDate, String lastAccessedAt) {
        this.id = id;
        this.originalName = originalName;
        this.path = path;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.downloadCount = downloadCount;
        this.userId = userId;
        this.uploadDate = uploadDate;
        this.lastAccessedAt = lastAccessedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(String lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FileRecord that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(originalName, that.originalName) && Objects.equals(path, that.path) && Objects.equals(contentType, that.contentType) && Objects.equals(fileSize, that.fileSize) && Objects.equals(downloadCount, that.downloadCount) && Objects.equals(userId, that.userId) && Objects.equals(uploadDate, that.uploadDate) && Objects.equals(lastAccessedAt, that.lastAccessedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, originalName, path, contentType, fileSize, downloadCount, userId, uploadDate, lastAccessedAt);
    }
}
