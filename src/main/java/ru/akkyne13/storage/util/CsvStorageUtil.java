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
    private CsvStorageUtil() {} // класс утилитарный, объекты создавать нельзя
    private enum BatchType {
        NORMAL,
        IMPORT,
        ARCHIVE
    }

    private static int parseAmount(String amountString, int lineNumber) throws CsvParseException {
        try {
            return Integer.parseInt(amountString);
        } catch (NumberFormatException e) {
            throw new CsvParseException(CsvParseException.CsvErrorCode.BAD_NUMBER, lineNumber);
        }
    }

    private static LocalDate parseDate(String dateString, int lineNumber) throws CsvParseException {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new CsvParseException(CsvParseException.CsvErrorCode.BAD_DATE, lineNumber);
        }
    }

    private static StorageItem parseLine(String line, int lineNumber) throws CsvParseException {
        String[] columns = line.split(";", -1); // TODO: сделать выбор сепаратора
        if (columns.length < 10) {
            throw new CsvParseException(CsvParseException.CsvErrorCode.WRONG_COLUMN_COUNT, lineNumber);
        }

        BatchType batchType;
        try {
            batchType = BatchType.valueOf(columns[0]);
        } catch (IllegalArgumentException e) {
            throw new CsvParseException(CsvParseException.CsvErrorCode.UNKNOWN_BATCH_TYPE, lineNumber);
        }

        String sku = columns[1];
        String name = columns[2];
        int amount = parseAmount(columns[3], lineNumber);
        String cell =  columns[4];
        LocalDate receiptDate = parseDate(columns[5], lineNumber);

        return switch (batchType) {
            case NORMAL -> new Batch(sku, name, amount, cell, receiptDate);

            case IMPORT -> {
                String country = columns[6];
                String city = columns[7];
                yield new ImportBatch(sku, name, amount, cell, receiptDate, country, city);
            }

            case ARCHIVE -> {
                LocalDate archiveDate = parseDate(columns[8], lineNumber);
                String archiveReason = columns[9];
                yield new ArchiveBatch(sku, name, amount, cell, receiptDate, archiveDate, archiveReason);
            }
        };
    }


    public static CsvLoadResult loadBatchesFromCsv(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path);

        List<StorageItem> validItems = new ArrayList<>();
        List<CsvParseException> errorMessages = new ArrayList<>();

        for (int lineNumber = 1; lineNumber < lines.size(); lineNumber++) {
            try {
                StorageItem item = parseLine(lines.get(lineNumber), lineNumber + 1);
                validItems.add(item);
            }
            catch (CsvParseException e) {
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
