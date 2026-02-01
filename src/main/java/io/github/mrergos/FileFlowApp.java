package io.github.mrergos;

import io.github.mrergos.controller.FileController;
import io.github.mrergos.controller.SecurityController;
import io.github.mrergos.dao.FileDao;
import io.github.mrergos.dao.UserDao;
import io.github.mrergos.service.BCryptUserSecurityService;
import io.github.mrergos.service.DiskFileService;
import io.github.mrergos.service.FileService;
import io.github.mrergos.service.UserSecurityService;
import io.javalin.Javalin;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FileFlowApp {

    private static final Logger log = LoggerFactory.getLogger(FileFlowApp.class);

    public static void main(String[] args) {
        FileFlowApp fileFlowApp = new FileFlowApp();
        fileFlowApp.run();
    }

    public void run() {
        Properties properties = getProperties();
        if (properties == null) return;

        String url = properties.getProperty("datasource.url", "jdbc:sqlite:storage.db");
        int port = Integer.parseInt(properties.getProperty("server.port", "8080"));

        flywayConfiguration(url);

        Jdbi jdbi = jdbiConfiguration(url);

        FileDao fileDao = jdbi.onDemand(FileDao.class);
        FileService fileService = new DiskFileService(fileDao, properties);

        UserDao userDao = jdbi.onDemand(UserDao.class);
        UserSecurityService userSecurityService = new BCryptUserSecurityService(userDao);

        schedulerConfiguration(fileService);

        FileController fileController = new FileController(fileService);
        SecurityController securityController = new SecurityController(userSecurityService);

        var app = Javalin.create(config -> {
            config.staticFiles.add("/public");
            config.http.maxRequestSize = 100 * 1024 * 1024L;
                })
                .post("/api/login", securityController::handleAuth)
                .post("/api/register", securityController::handleRegistration)
                .post("/api/logout", securityController::handleLogout)
                .before("/api/upload", securityController::handleSession)
                .post("/api/upload", fileController::handleUploadFile)
                .get("/api/download/{id}", fileController::handleDownloadFile)
                .get("/api/stats", fileController::handleStats)
                .get("/api/me", securityController::handleGetCurrentUser)
                .start(port);
    }

    @Nullable
    private static Properties getProperties() {
        Properties properties = new Properties();

        try (InputStream is = FileFlowApp.class.getClassLoader().getResourceAsStream("properties.properties")) {
            if (is == null) {
                log.error("properties.properties was not found");
                return null;
            }

            properties.load(is);
        } catch (IOException e) {
            log.error(e.toString());
        }
        return properties;
    }

    private static void schedulerConfiguration(FileService fileService) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable cleanUpTask = () -> {
            log.info("Starting cleanup");
            fileService.deleteExpiredFiles();
            log.info("Cleanup finished");
        };

        scheduler.scheduleAtFixedRate(cleanUpTask, 0, 24, TimeUnit.HOURS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown scheduler");
            scheduler.shutdown();
        }));
    }

    @NotNull
    private static Jdbi jdbiConfiguration(String url) {
        Jdbi jdbi = Jdbi.create(url);
        jdbi.installPlugin(new SqlObjectPlugin());
        return jdbi;
    }

    private static void flywayConfiguration(String url) {
        Flyway flyway = Flyway.configure()
                .dataSource(url, null, null)
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }
}