package ru.akkyne13.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {
    public static List<StorageItem> loadBatchesFromCsv(String filePath) throws CsvParseException, IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);

        List<StorageItem> items = new ArrayList<>();

        for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
            String[] columns = lines.get(lineNumber).split(";", -1); // TODO: сделать выбор сепаратора
            if (columns.length < 6) {
                throw new CsvParseException(CsvParseException.CsvErrorCode.WRONG_COLUMN_COUNT, lineNumber+1);
            }

            String sku = columns[1];
            String name = columns[2];

            int amount = 0;
            try {
                amount = Integer.parseInt(columns[3]);
            } catch (NumberFormatException e) {
                throw new CsvParseException(CsvParseException.CsvErrorCode.BAD_NUMBER, lineNumber + 1);
            }

            String cell =  columns[4];

            LocalDate receiptDate = null;
            try {
                receiptDate = LocalDate.parse(columns[5], DateTimeFormatter.ofPattern("dd.MM.yy")); // TODO: сделать выбор формата даты
            } catch (DateTimeParseException e) {
                throw new CsvParseException(CsvParseException.CsvErrorCode.BAD_DATE, lineNumber + 1);
            }

            // TODO: ввынести функционал создания партии в отдельную функцию
            switch (columns[0]) {
                case "NORMAL":
                    Batch batch = new Batch(sku, name, amount, cell, receiptDate);
                    items.add(batch);
                    continue;
                case "IMPORT":
                    if (columns.length < 8) {
                        throw new CsvParseException(CsvParseException.CsvErrorCode.WRONG_COLUMN_COUNT, lineNumber + 1);
                    }

                    String country = columns[6];
                    String customsCode = columns[7];

                    ImportBatch importBatch = new ImportBatch(sku, name, amount, cell, receiptDate, country, customsCode);
                    items.add(importBatch);
                    continue;
                case "ARCHIVE":
                    if (columns.length < 10) {
                        throw new CsvParseException(CsvParseException.CsvErrorCode.WRONG_COLUMN_COUNT, lineNumber + 1);
                    }

                    LocalDate archiveDate = null;
                    try{
                        archiveDate = LocalDate.parse(columns[8], DateTimeFormatter.ofPattern("yy.MM.dd"));
                    } catch (DateTimeParseException e) {
                        throw new CsvParseException(CsvParseException.CsvErrorCode.BAD_DATE, lineNumber + 1);
                    }

                    String archiveReason = columns[9];

                    ArchiveBatch archiveBatch = new ArchiveBatch(sku, name, amount, cell, receiptDate,  archiveDate, archiveReason);
                    items.add(archiveBatch);
                    continue;
                default:
                    throw new CsvParseException(CsvParseException.CsvErrorCode.UNKNOWN_BATCH_TYPE, lineNumber+1);
            }
        }

        return items;
    }
}
