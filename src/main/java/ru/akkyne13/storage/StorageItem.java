package ru.akkyne13.storage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface StorageItem {
    String getSku();
    String getName();
    int getAmount();
    String getCell();
    LocalDate getReceiptDate();

    DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy");
    String toCsvRow();
}
