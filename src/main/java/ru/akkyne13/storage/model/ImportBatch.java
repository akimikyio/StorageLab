package ru.akkyne13.storage.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ImportBatch extends Batch {
    private String country;
    private String customsCode;

    public ImportBatch() { }

    public ImportBatch(String sku, String name, int amount, String cell, LocalDate receiptDate, String country, String customsCode) {
        super(sku, name, amount, cell, receiptDate);
        this.country = country;
        this.customsCode = customsCode;
    }

    // Getters
    public String getCountry() {
        return country;
    }

    public String getCustomsCode() {
        return customsCode;
    }

    // Setters
    public void setCountry(String country) {
        this.country = country;
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
            errors.add("Пустой таможенный код");
        }

        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ImportBatch that = (ImportBatch) o;
        return Objects.equals(country, that.country) && Objects.equals(customsCode, that.customsCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), country, customsCode);
    }
}
