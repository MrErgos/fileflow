CREATE TABLE files
(
    id               VARCHAR PRIMARY KEY,
    original_name             VARCHAR NOT NULL,
    path             VARCHAR NOT NULL,
    content_type     VARCHAR,
    file_size BIGINT NOT NULL,
    download_count INT NOT NULL DEFAULT 0,
    user_id INTEGER NOT NULL,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_files_last_accessed_at ON files(last_accessed_at);