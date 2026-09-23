package ru.akkyne13.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Batch implements Editable, StorageItem {
    private String sku;
    private String name;
    private int amount;
    private String cell;
    private LocalDate receiptDate;

    public Batch(String sku, String name, int amount, String cell, LocalDate receiptDate) {
        this.sku = sku;
        this.name = name;
        this.amount = amount;
        this.cell = cell;
        this.receiptDate = receiptDate;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDate receiptDate) {
        this.receiptDate = receiptDate;
    }

    @Override
    public String toString() {
        return "SKU: " + this.sku +
                ", name: " + this.name +
                ", amount: " + this.amount +
                ", cell: " + this.cell +
                ", receiptDate: " + this.receiptDate;
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        if (sku == null || sku.isBlank()) {
            errors.add("Пустой артикул");
        }
        if (name == null || name.isBlank()) {
            errors.add("Пустое наименование");
        }
        if (amount <= 0) {
            errors.add("Количество не может быть отрицательным");
        }
        if (cell == null || cell.isBlank()) {
            errors.add("Пустой код ячейки");
        }
        if (receiptDate == null) {
            errors.add("Пустая дата поступления");
        }

        return errors;
    }
}
