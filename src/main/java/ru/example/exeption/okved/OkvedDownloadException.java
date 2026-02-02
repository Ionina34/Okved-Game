package ru.example.exeption.okved;

import lombok.Getter;

/**
 * Проблемы с загрузкой файла (сеть, таймаут, HTTP ≠ 200, 404, 503 и т.д.)
 */
@Getter
public class OkvedDownloadException extends OkvedException {

    private final String url;
    private final int httpStatus;
    private final String responsePreview;


    public OkvedDownloadException(String url, int httpStatus, String responseBody) {
        this(url, httpStatus, responseBody, null);
    }

    public OkvedDownloadException(String url, int httpStatus, String responseBody, Throwable cause) {
        super(buildMessage(url, httpStatus, responseBody), cause);
        this.url = url;
        this.httpStatus = httpStatus;
        this.responsePreview = responseBody != null ? responseBody : "(no response body)";
    }

    private static String buildMessage(String url, int status, String body) {
        return String.format(
                "Не удалось загрузить справочник ОКВЭД\n" +
                        "URL: %s\n" +
                        "HTTP статус: %d\n" +
                        "Первые символы ответа: %s",
                url, status, body);
    }
}
