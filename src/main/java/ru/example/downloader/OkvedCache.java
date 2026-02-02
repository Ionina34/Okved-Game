package ru.example.downloader;

import ru.example.exeption.okved.OkvedException;
import ru.example.model.Okved;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Ленивый кэш справочника ОКВЭД с обновлением раз в сутки
 */
public class OkvedCache {

    private static final Duration CACHE_TTL = Duration.ofHours(24);
    private static volatile OkvedCache instance;

    private List<Okved> okveds;
    private Instant lastLoaded = Instant.MIN;
    private final String url;

    private OkvedCache(String url) {
        this.url = url;
    }

    public static OkvedCache getInstance(String url) {
        if (instance == null) {
            synchronized (OkvedCache.class) {
                if (instance == null) {
                    instance = new OkvedCache(url);
                }
            }
        }
        return instance;
    }

    /**
     * Возвращает актуальный список ОКВЭД.
     * Если кэш пустой или устарел — загружает заново.
     */
    public List<Okved> getOkveds() throws IOException, OkvedException {
        Instant now = Instant.now();

        if (okveds == null || Duration.between(lastLoaded, now).compareTo(CACHE_TTL) >= 0) {
            synchronized (this){
                if(okveds == null || Duration.between(lastLoaded, now).compareTo(CACHE_TTL) >= 0){
                    System.out.println("Загружаем / обновляем справочник ОКВЭД...");
                    okveds = OkvedDownloader.downloaderAndParse(url);
                    lastLoaded = Instant.now();
                    System.out.println("Справочник загружен (" + okveds.size() + " элементов)");
                }
            }
        }
        return okveds;
    }

    /**
     * Принудительное обновление кэша (можно вызвать по команде в игре)
     */
    public void forceRefresh() throws IOException, OkvedException {
        synchronized (this) {
            okveds = OkvedDownloader.downloaderAndParse(url);
            lastLoaded = Instant.now();
            System.out.println("Справочник принудительно обновлён");
        }
    }
}
