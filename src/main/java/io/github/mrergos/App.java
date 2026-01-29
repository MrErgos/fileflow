package io.github.mrergos;

import io.github.mrergos.controller.FileController;
import io.github.mrergos.dao.FileDao;
import io.github.mrergos.service.DiskFileService;
import io.github.mrergos.service.FileService;
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

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    public void run() {
        Properties properties = getProperties();
        if (properties == null) return;

        String url = properties.getProperty("datasource.url", "jdbc:sqlite:storage.db");
        int port = Integer.parseInt(properties.getProperty("server.port", "8080"));

        flywayConfiguration(url);

        Jdbi jdbi = jdbiConfiguration(url);

        FileDao dao = jdbi.onDemand(FileDao.class);
        FileService fileService = new DiskFileService(dao, properties);

        schedulerConfiguration(fileService);

        FileController controller = new FileController(fileService);
        var app = Javalin.create()
                .post("/upload", controller::handleUploadFile)
                .get("/download/{id}", controller::handleDownloadFile)
                .start(port);
    }

    @Nullable
    private static Properties getProperties() {
        Properties properties = new Properties();

        try (InputStream is = App.class.getClassLoader().getResourceAsStream("properties.properties")) {
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