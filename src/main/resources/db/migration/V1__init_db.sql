CREATE TABLE files
(
    id               VARCHAR PRIMARY KEY,
    original_name             VARCHAR NOT NULL,
    path             VARCHAR NOT NULL,
    content_type     VARCHAR,
    file_size BIGINT NOT NULL,
    download_count INT NOT NULL DEFAULT 0,
    last_accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_files_last_accessed_at ON files(last_accessed_at);