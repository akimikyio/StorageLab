package ru.akkyne13.storage.model;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ArchiveBatch(
        String sku,
        String name,
        int amount,
        String cell,
        LocalDate receiptDate,
        LocalDate archiveDate,
        String archiveReason,
        String note,
        LocalDateTime updatedAt
) implements StorageItem {
    public ArchiveBatch(
            String sku,
            String name,
            int amount,
            String cell,
            LocalDate receiptDate,
            LocalDate archiveDate,
            String archiveReason
    ) {
        this(sku, name, amount, cell, receiptDate, archiveDate, archiveReason,
                "", receiptDate != null ? receiptDate.atStartOfDay() : null);
    }


    @Serial
    private static final long serialVersionUID = 1L;

    // Getters
    @Override
    public String getSku() {
        return sku;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public String getCell() {
        return cell;
    }

    @Override
    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
