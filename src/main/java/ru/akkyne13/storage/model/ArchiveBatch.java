package ru.akkyne13.storage.model;

import java.time.LocalDate;

public record ArchiveBatch(
        String sku,
        String name,
        int amount,
        String cell,
        LocalDate receiptDate,
        LocalDate archiveDate,
        String archiveReason) implements StorageItem {

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
    public String toCsvRow() {
        String formattedReceiptDate = getReceiptDate() != null ? getReceiptDate().format(DATE_FORMATTER) : "";
        String formattedArchiveDate = archiveDate != null ? archiveDate.format(DATE_FORMATTER) : "";
        return String.join(";",
                "ARCHIVE",
                getSku(),
                getName(),
                String.valueOf(getAmount()),
                getCell(),
                formattedReceiptDate,
                "", "",
                formattedArchiveDate,
                archiveReason != null ? archiveReason : ""
        );
    }
}
