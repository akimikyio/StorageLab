package ru.akkyne13.storage;

import ru.akkyne13.storage.io.*;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.task.StorageItemFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class StorageSerializationBenchmark {
    private static final int ITEMS_COUNT = 10_000;
    private static final int RUNS = 5;

    private static final String SEPARATOR = "+--------+------------+---------------+---------------+---------------+";
    private static final String HEADER    = "| Формат | Прогон     | Запись (мс)   | Чтение (мс)   | Размер (КБ)   |";
    private static final String ROW_FMT   = "| %-6s | %-10s | %13d | %13d | %13d |%n";
    private static final String AVG_FMT   = "| %-6s | %-10s | %13.1f | %13.1f | %13.1f |%n";

    public static void main(String[] args) throws Exception {
        StorageItem[] items = new StorageItem[ITEMS_COUNT];
        long timestamp = System.currentTimeMillis() % 100000;

        for (int i = 0; i < ITEMS_COUNT; i++) {
            String sku = String.format("SKU-%d-%d", timestamp, i);
            items[i] = StorageItemFactory.createRandomItem(i, sku);
        }

        StorageData data = new StorageData(1, items);
        System.out.printf("Сгенерировано записей: %d (прогонов на каждый формат: %d)%n%n", ITEMS_COUNT, RUNS);

        Path dir = Path.of("benchmark_results");
        Files.createDirectories(dir);

        List<Serializer<StorageData>> serializers = List.of(
                new CsvSerializer(),
                new JsonSerializer(),
                new BinSerializer()
        );

        System.out.println(SEPARATOR);
        System.out.println(HEADER);
        System.out.println(SEPARATOR);

        for (Serializer<StorageData> serializer : serializers) {
            Path file = dir.resolve("data." + serializer.format());
            runBenchmark(serializer, data, file);
        }
    }

    private static void runBenchmark(Serializer<StorageData> serializer, StorageData data, Path file) throws Exception {
        String formatName = serializer.format().toUpperCase();

        serializer.save(data, file);
        serializer.load(file);

        long totalSaveMs = 0;
        long totalLoadMs = 0;
        long totalSizeKb = 0;

        for (int i = 1; i <= RUNS; i++) {
            long startSave = System.currentTimeMillis();
            serializer.save(data, file);
            long saveTime = System.currentTimeMillis() - startSave;

            long fileSizeKb = Files.size(file) / 1024;

            long startLoad = System.currentTimeMillis();
            serializer.load(file);
            long loadTime = System.currentTimeMillis() - startLoad;

            totalSaveMs += saveTime;
            totalLoadMs += loadTime;
            totalSizeKb += fileSizeKb;

            System.out.printf(ROW_FMT, formatName, "Прогон " + i, saveTime, loadTime, fileSizeKb);
        }

        double avgSave = (double) totalSaveMs / RUNS;
        double avgLoad = (double) totalLoadMs / RUNS;
        double avgSize = (double) totalSizeKb / RUNS;

        System.out.println(SEPARATOR);
        System.out.printf(AVG_FMT, formatName, "СРЕДНЕЕ", avgSave, avgLoad, avgSize);
        System.out.println(SEPARATOR);
    }
}
