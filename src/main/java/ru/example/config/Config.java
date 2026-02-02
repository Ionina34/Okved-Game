package ru.example.config;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Конфигурационный класс для загрузки ссылки на ресурс из config.properties
 */
public class Config {
    @Getter
    private static String okvedJsonUrl;

    static {
        Properties props = new Properties();
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Конфигурационный файл не найден.");
            } else {
                props.load(input);
                okvedJsonUrl = props.getProperty("okved.json-url");
                if (okvedJsonUrl == null) {
                    System.err.println("'okved.json-url' не найден");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            okvedJsonUrl = "https://raw.githubusercontent.com/bergstar/testcase/refs/heads/master/okved.json";  // fallback на ошибку
        }
    }

}
