package ru.akkyne13.storage.util;

import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.model.ArchiveBatch;
import ru.akkyne13.storage.model.Batch;
import ru.akkyne13.storage.model.ImportBatch;
import ru.akkyne13.storage.model.StorageItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static ru.akkyne13.storage.model.StorageItem.DATE_FORMATTER;

public class CsvStorageUtil {
    public static CsvLoadResult loadBatchesFromCsv(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);

        List<StorageItem> validItems = new ArrayList<>();
        List<CsvParseException> errorMessages = new ArrayList<>();

        for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
            try {
                String[] columns = lines.get(lineNumber).split(";", -1); // TODO: сделать выбор сепаратора
                if (columns.length < 10) {
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
                    receiptDate = LocalDate.parse(columns[5], DATE_FORMATTER); // TODO: сделать выбор формата даты
                } catch (DateTimeParseException e) {
                    throw new CsvParseException(CsvParseException.CsvErrorCode.BAD_DATE, lineNumber + 1);
                }

                // TODO: ввынести функционал создания партии в отдельную функцию
                switch (columns[0]) {
                    case "NORMAL":
                        Batch batch = new Batch(sku, name, amount, cell, receiptDate);
                        validItems.add(batch);
                        continue;

                    case "IMPORT":
                        String country = columns[6];
                        String customsCode = columns[7];

                        ImportBatch importBatch = new ImportBatch(sku, name, amount, cell, receiptDate, country, customsCode);
                        validItems.add(importBatch);
                        continue;

                    case "ARCHIVE":
                        LocalDate archiveDate = null;
                        try{
                            archiveDate = LocalDate.parse(columns[8], DATE_FORMATTER);
                        } catch (DateTimeParseException e) {
                            throw new CsvParseException(CsvParseException.CsvErrorCode.BAD_DATE, lineNumber + 1);
                        }

                        String archiveReason = columns[9];

                        ArchiveBatch archiveBatch = new ArchiveBatch(sku, name, amount, cell, receiptDate,  archiveDate, archiveReason);
                        validItems.add(archiveBatch);
                        continue;

                    default:
                        throw new CsvParseException(CsvParseException.CsvErrorCode.UNKNOWN_BATCH_TYPE, lineNumber+1);
                }
            } catch (CsvParseException e) { // TODO: сделать вывод списка битых строк визуально
                errorMessages.add(e);
            }
        }

        return new CsvLoadResult(validItems, errorMessages);
    }

    public static void saveBatchesToCsv(List<StorageItem> items, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = new ArrayList<>();

        lines.add("Тип партии;Артикул;Наименование;Количество;Ячейка;Дата поступления;Страна;Таможенный код;Дата архивации;Причина архивации");

        for (StorageItem item : items) {
            lines.add(item.toCsvRow());
        }

        Files.write(path, lines);
    }
}
