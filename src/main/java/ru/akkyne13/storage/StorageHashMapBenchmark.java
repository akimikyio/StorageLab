package ru.akkyne13.storage;

import ru.akkyne13.storage.model.Batch;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.structure.OpenAddressingHashMap;
import ru.akkyne13.storage.structure.StorageMap;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class StorageHashMapBenchmark {
    public static void main(String[] args) {
        warmup();

        System.out.println("\n=== ЗАМЕРЫ ДЛЯ 10 000 ЭЛЕМЕНТОВ ===");
        runBenchmark(10_000);

        System.out.println("\n=== ЗАМЕРЫ ДЛЯ 100 000 ЭЛЕМЕНТОВ ===");
        runBenchmark(100_000);
    }

    private static void runBenchmark(int size) {
        String[] keys = new String[size];
        StorageItem[] items = new StorageItem[size];

        for (int i = 0; i < size; i++) {
            keys[i] = "SKU-BENCH-" + i;
            items[i] = new Batch(keys[i], "TestItem", i, "A1", LocalDate.now());
        }

        // OpenAddressingHashMap
        StorageMap<String, StorageItem> customMap = new OpenAddressingHashMap<>();

        long startPut = System.nanoTime();
        for (int i = 0; i < size; i++) customMap.put(keys[i], items[i]);
        long timePut = System.nanoTime() - startPut;

        long startGet = System.nanoTime();
        for (int i = 0; i < size; i++) customMap.get(keys[i]);
        long timeGet = System.nanoTime() - startGet;

        long startDel = System.nanoTime();
        for (int i = 0; i < size; i++) customMap.delete(keys[i]);
        long timeDel = System.nanoTime() - startDel;

        // java.util.HashMap
        Map<String, StorageItem> javaMap = new ConcurrentHashMap<>();

        long startPutJava = System.nanoTime();
        for (int i = 0; i < size; i++) javaMap.put(keys[i], items[i]);
        long timePutJava = System.nanoTime() - startPutJava;

        long startGetJava = System.nanoTime();
        for (int i = 0; i < size; i++) javaMap.get(keys[i]);
        long timeGetJava = System.nanoTime() - startGetJava;

        long startDelJava = System.nanoTime();
        for (int i = 0; i < size; i++) javaMap.remove(keys[i]);
        long timeDelJava = System.nanoTime() - startDelJava;

        // Вывод результатов
        System.out.printf("%-15s | %-20s | %-20s%n", "Операция", "OpenAddressingMap (мс)", "java.util.HashMap (мс)");
        System.out.println("-".repeat(60));
        System.out.printf("%-15s | %-20.2f | %-20.2f%n", "Вставка (put)", timePut / 1_000_000.0, timePutJava / 1_000_000.0);
        System.out.printf("%-15s | %-20.2f | %-20.2f%n", "Поиск (get)", timeGet / 1_000_000.0, timeGetJava / 1_000_000.0);
        System.out.printf("%-15s | %-20.2f | %-20.2f%n", "Удаление (del)", timeDel / 1_000_000.0, timeDelJava / 1_000_000.0);
    }

    private static void warmup() {
        StorageMap<String, StorageItem> map = new OpenAddressingHashMap<>();
        for (int i = 0; i < 5000; i++) map.put("W-" + i, new Batch("W-" + i, "W", 1, "W", LocalDate.now()));
        for (int i = 0; i < 5000; i++) map.get("W-" + i);
        for (int i = 0; i < 5000; i++) map.delete("W-" + i);
    }
}
