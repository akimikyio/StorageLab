package ru.akkyne13.storage;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

public class StorageTableView extends TableView<StorageItem> {
    public StorageTableView() {
        TableColumn<StorageItem, String> skuCol = new TableColumn<>("Артикул");
        skuCol.setCellValueFactory(new PropertyValueFactory<>("sku"));

        TableColumn<StorageItem, String> nameCol = new TableColumn<>("Наименование");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StorageItem, Integer> amountCol = new TableColumn<>("Количество");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<StorageItem, String> cellCol = new TableColumn<>("Ячейка");
        cellCol.setCellValueFactory(new PropertyValueFactory<>("cell"));

        TableColumn<StorageItem, LocalDate> receiptDateCol = new TableColumn<>("Дата поступления");
        receiptDateCol.setCellValueFactory(new PropertyValueFactory<>("receiptDate"));

        TableColumn<StorageItem, String> countryCol = new TableColumn<>("Страна-отправитель");
        countryCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ImportBatch importBatch) {
                return new SimpleStringProperty(importBatch.getCountry());
            }

            return new SimpleStringProperty(null);
        });

        TableColumn<StorageItem, String> customsCodeCol = new TableColumn<>("Таможенный код");
        customsCodeCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ImportBatch importBatch) {
                return new SimpleStringProperty(importBatch.getCustomsCode());
            }

            return new SimpleStringProperty(null);
        });

        TableColumn<StorageItem, LocalDate> archiveDateCol = new TableColumn<>("Дата архивации");
        archiveDateCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ArchiveBatch archiveBatch) {
                return new SimpleObjectProperty<>(archiveBatch.archiveDate());
            }

            return new SimpleObjectProperty<>(null);
        });

        TableColumn<StorageItem, String> archiveReasonCol = new TableColumn<>("Причина архивации");
        archiveReasonCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ArchiveBatch archiveBatch) {
                return new SimpleStringProperty(archiveBatch.archiveReason());
            }

            return new SimpleStringProperty(null);
        });

        this.getColumns().addAll(List.of(skuCol, nameCol, amountCol,
                cellCol, receiptDateCol, countryCol,
                customsCodeCol, archiveDateCol, archiveReasonCol));

    }
}
