package io.github.mrergos.dao;

import io.github.mrergos.entity.FileRecord;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

public interface FileDao {
    @SqlUpdate("INSERT INTO files (id, original_name, path, content_type, file_size, user_id) VALUES (:id, :original_name, :path, :content_type, :file_size, :user_id)")
    void insertFile(@Bind("id") String id, @Bind("original_name") String originalName, @Bind("path") String path, @Bind("content_type") String contentType, @Bind("file_size") Long fileSize, @Bind("user_id") Long userId);

    @SqlUpdate("UPDATE files SET last_accessed_at = CURRENT_TIMESTAMP, download_count = download_count + 1 WHERE id = :id")
    void updateFileMeta(@Bind("id") String id);

    @SqlQuery("SELECT * FROM files WHERE id = :id")
    @RegisterBeanMapper(FileRecord.class)
    FileRecord findFile(@Bind("id") String id);

    @SqlQuery("SELECT * FROM files WHERE last_accessed_at < datetime('now', :interval)")
    @RegisterBeanMapper(FileRecord.class)
    List<FileRecord> findExpiredFiles(@Bind("interval") String interval);

    @SqlUpdate("DELETE FROM files WHERE id IN (<ids>)")
    void deleteFiles(@BindList("ids") List<String> ids);

    @SqlQuery("SELECT * FROM files where user_id = :user_id ORDER BY upload_date DESC")
    @RegisterBeanMapper(FileRecord.class)
    List<FileRecord> findUserFiles(@Bind("user_id") Long userId);
}
