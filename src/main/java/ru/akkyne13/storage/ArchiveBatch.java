package ru.akkyne13.storage;

import java.time.LocalDate;

public record ArchiveBatch(
        String sku,
        String name,
        int amount,
        String cell,
        LocalDate receiptDate,
        LocalDate archiveDate,
        String archiveReason) implements StorageItem {
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
}
