package ru.example.downloader;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.example.exeption.okved.OkvedDownloadException;
import ru.example.exeption.okved.OkvedEmptyException;
import ru.example.exeption.okved.OkvedParseException;
import ru.example.model.Okved;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OkvedDownloaderTest {

    private static HttpServer server;
    private static String baseUrl;

    @BeforeAll
    static void init(@TempDir Path tempDir) throws Exception {
        // Создаём временные файлы с разным содержимым
        Path validFile = tempDir.resolve("valid.json");
        Files.writeString(validFile, """
                [
                  {"code":"A","name":"Test","items":[
                    {"code":"01","name":"Sub"}
                  ]}
                ]
                """);

        Path emptyFile = tempDir.resolve("empty.json");
        Files.writeString(emptyFile, "[]");

        Path invalidFile = tempDir.resolve("invalid.json");
        Files.writeString(invalidFile, "{invalid json here}");

        // Запускаем мини-сервер
        server = HttpServer.create(new InetSocketAddress(0), 0);

        // Контекст для валидного JSON
        server.createContext("/valid.json", exchange -> {
            byte[] bytes = Files.readAllBytes(validFile);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            exchange.close();
        });

        // Контекст для пустого массива
        server.createContext("/empty.json", exchange -> {
            byte[] bytes = Files.readAllBytes(emptyFile);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            exchange.close();
        });

        // Контекст для битого JSON
        server.createContext("/invalid.json", exchange -> {
            byte[] bytes = Files.readAllBytes(invalidFile);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            exchange.close();
        });

        server.start();
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    static void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void downloadAndParse_validJson_shouldReturnFlattenedList() throws Exception {
        String url = baseUrl + "/valid.json";
        List<Okved> result = OkvedDownloader.downloaderAndParse(url);

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getCode());
        assertEquals("01", result.get(1).getCode());
    }

    @Test
    void downloadAndParse_emptyJson_shouldThrowOkvedEmptyException() {
        String url = baseUrl + "/empty.json";
        assertThrows(OkvedEmptyException.class,
                () -> OkvedDownloader.downloaderAndParse(url));
    }

    @Test
    void downloadAndParse_invalidJson_shouldThrowOkvedParseException() {
        String url = baseUrl + "/invalid.json";
        assertThrows(OkvedParseException.class,
                () -> OkvedDownloader.downloaderAndParse(url));
    }

    @Test
    void downloadAndParse_nonExistentPath_shouldThrowOkvedDownloadException() {
        String url = baseUrl + "/non-existent.json";
        assertThrows(OkvedDownloadException.class,
                () -> OkvedDownloader.downloaderAndParse(url));
    }
}