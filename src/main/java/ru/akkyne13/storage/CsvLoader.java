package ru.akkyne13.storage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// TODO: переписать парсер под первую колонку - тип партии
public class CsvLoader {
    public static List<StorageItem> loadBatchesFromCsv(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);

        List<StorageItem> items = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] columns = lines.get(i).split(";", -1); // TODO: сделать выбор сепаратора
            String sku = columns[0];
            String name = columns[1];
            int amount = Integer.parseInt(columns[2]);
            String cell =  columns[3];
            LocalDate receiptDate = LocalDate.parse(columns[4], DateTimeFormatter.ofPattern("dd.MM.yy")); // TODO: сделать выбор формата даты

            String country = null;
            String customsCode = null;

            if (!columns[5].isEmpty()) {
                country = columns[5];
                customsCode = columns[6];

                ImportBatch importBatch = new ImportBatch(sku, name, amount, cell, receiptDate, country, customsCode);
                items.add(importBatch);
                continue;
            }

            LocalDate archiveDate = null;
            String archiveReason = null;

            if (!columns[7].isEmpty()) {
                archiveDate = LocalDate.parse(columns[7], DateTimeFormatter.ofPattern("yy.MM.dd"));
                archiveReason = columns[8];

                ArchiveBatch archiveBatch = new ArchiveBatch(sku, name, amount, cell, receiptDate,  archiveDate, archiveReason);
                items.add(archiveBatch);
                continue;
            }

            Batch batch = new Batch(sku, name, amount, cell, receiptDate);
            items.add(batch);
        }

        return items;
    }
}
