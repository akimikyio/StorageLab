package ru.akkyne13.storage;

import java.time.LocalDate;
import java.util.List;

public class ImportBatch extends Batch implements StorageItem {
    private String country;
    private String customsCode;

    public ImportBatch(String sku, String name, int amount, String cell, LocalDate receiptDate, String country, String customsCode) {
        super(sku, name, amount, cell, receiptDate);
        this.country = country;
        this.customsCode = customsCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    public void setCustomsCode(String customsCode) {
        this.customsCode = customsCode;
    }

    @Override
    public String toString() {
        return "SKU: " + this.getSku() +
                ", name: " + this.getName() +
                ", amount: " + this.getAmount() +
                ", cell: " + this.getCell() +
                ", receiptDate: " + this.getReceiptDate() +
                ", country: " + this.getCountry() +
                ", customsCode: " + this.getCustomsCode();
    }

    @Override
    public List<String> validate() {
        List<String> errors = super.validate();

        if (country == null || country.isBlank()) {
            errors.add("Пустая страна-отправитель");
        }

        if (customsCode == null || customsCode.isBlank()) {
            errors.add("Пустой код страны");
        }

        return errors;
    }
}
