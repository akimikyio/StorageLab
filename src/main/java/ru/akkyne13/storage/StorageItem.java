package ru.akkyne13.storage;

import java.time.LocalDate;

public interface StorageItem {
    String getSku();
    String getName();
    int getAmount();
    String getCell();
    LocalDate getReceiptDate();
}
