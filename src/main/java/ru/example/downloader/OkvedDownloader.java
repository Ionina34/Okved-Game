package ru.example.downloader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.example.exeption.okved.OkvedEmptyException;
import ru.example.exeption.okved.OkvedException;
import ru.example.exeption.okved.OkvedParseException;
import ru.example.exeption.okved.OkvedDownloadException;
import ru.example.model.Okved;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Загрузчик и парсер справочника ОКВЭД из JSON-файла
 */
public class OkvedDownloader {

    private static final Gson GSON = new Gson();

    /**
     * Загружает и парсит иерархический справочник ОКВЭД по указанному URL
     *
     * @param url полный URL на файл okved.json
     * @return полный список всех узлов ОКВЭД
     * @throws IOException при проблемах с сетью
     * @throws OkvedException при проблемах с HTTP-статусом (не 200) или некорректном JSON
     */
    public static List<Okved> downloaderAndParse(String url) throws IOException, OkvedException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new OkvedDownloadException(url,
                        response.statusCode(),
                        response.body().substring(0, Math.min(200, response.body().length()))
                );
            }

            String json = response.body();

            //Корневой элемент - массив
            List<Okved> rootSections;
            try {
                rootSections = GSON.fromJson(json, new TypeToken<List<Okved>>() {
                }.getType());
            } catch (Exception e) {
                throw new OkvedParseException("Не удалось распарсить JSON как  иерархический справочник ОКВЭД",
                        e,
                        json.substring(0, Math.min(200, json.length()))
                );
            }

            //Собираем плоский список всех узлов
            List<Okved> allOkveds = new ArrayList<>();
            flatten(rootSections, allOkveds);

            if (allOkveds.isEmpty()) {
                throw new OkvedEmptyException("Справочник ОКВЭД пустой после парсинга");
            }
            return allOkveds;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Прервано во время загрузки ОКВЭД", e);
        }
    }

    private static void flatten(List<Okved> nodes, List<Okved> target) {
        if (nodes == null) return;
        for (Okved node : nodes) {
            target.add(node);
            flatten(node.getChildren(), target);
        }
    }
}
