package ru.akkyne13.storage;

import java.time.LocalDate;

public record ArchiveBatch(
        String sku,
        String name,
        int amount,
        String cell,
        LocalDate receiptDate,
        LocalDate archiveDate,
        String archiveReason) implements StorageItem { }
