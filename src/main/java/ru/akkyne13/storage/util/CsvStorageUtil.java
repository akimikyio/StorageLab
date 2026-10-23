package ru.akkyne13.storage.util;

import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.model.ArchiveBatch;
import ru.akkyne13.storage.model.Batch;
import ru.akkyne13.storage.model.ImportBatch;
import ru.akkyne13.storage.model.StorageItem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;


public class CsvStorageUtil {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final Map<Class<? extends StorageItem>, CsvRowMapper<? extends StorageItem>> MAPPERS = new HashMap<>();

    static {
        MAPPERS.put(Batch.class, new BatchCsvMapper());
        MAPPERS.put(ImportBatch.class, new ImportBatchCsvMapper());
        MAPPERS.put(ArchiveBatch.class, new ArchiveBatchCsvMapper());
    }

    private CsvStorageUtil() { } // класс утилитарный, объекты создавать нельзя

    @SuppressWarnings("unchecked")
    public static <T extends StorageItem> String toCsvRow(T item) {
        CsvRowMapper<T> mapper = (CsvRowMapper<T>) MAPPERS.get(item.getClass());
        if (mapper == null) {
            throw new IllegalArgumentException("Нет CSV маппера для класса: " + item.getClass().getSimpleName());
        }
        return mapper.map(item);
    }

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
        String cell = columns[4];
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


    public static CsvLoadResult loadBatchesFromCsv(String filePath, BiConsumer<Integer, Integer> progressUpdater) throws IOException {
        Path path = Paths.get(filePath);

        int lineCount = 0;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            while (reader.readLine() != null) {
                lineCount++;
            }
        }

        StorageItem[] validItems = new StorageItem[lineCount];
        CsvParseException[] errors = new CsvParseException[lineCount];

        int validIndex = 0;
        int errorIndex = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            int currentLineNumber = 1;

            if ((line = reader.readLine()) != null) {
                currentLineNumber++;

                while ((line = reader.readLine()) != null) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    try {
                        StorageItem item = parseLine(line, currentLineNumber);
                        validItems[validIndex++] = item;
                    } catch (CsvParseException e) {
                        errors[errorIndex++] = e;
                    }

                    if (progressUpdater != null && currentLineNumber % 100 == 0) {
                        progressUpdater.accept(currentLineNumber, lineCount);
                    }
                    currentLineNumber++;
                }
            }
        }
        return new CsvLoadResult(
                Arrays.copyOf(validItems, validIndex),
                Arrays.copyOf(errors, errorIndex)
        );

    }

    public static void saveBatchesToCsv(StorageItem[] items, String filePath) throws IOException {
        Path path = Paths.get(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("Тип партии;Артикул;Наименование;Количество;Ячейка;Дата поступления;Страна;Таможенный код;Дата архивации;Причина архивации\n");

            for (StorageItem item : items) {
                writer.write(toCsvRow(item) + "\n");
            }
        }
    }
}
