package ru.akkyne13.storage;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StorageApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<Batch> batches = new ArrayList<>();
        batches.add(new Batch("ОЗН501256023", "Попрыгунчик", 300, "CY1725", LocalDate.of(2026, 4, 7)));
        batches.add(new Batch("ОЗН234525472", "Чайник", 40, "AH2501", LocalDate.of(2026, 3, 16)));

        List<ImportBatch> importBatches = new ArrayList<>();
        importBatches.add(new ImportBatch("ОЗН501256023", "Попрыгунчик", 300, "CY1725",
                LocalDate.of(2026, 4, 7), "China", "HZ322228"));

        List<ArchiveBatch> archiveBatches = new ArrayList<>();
        archiveBatches.add(new ArchiveBatch("ОЗН234525472", "Чайник", 40, "AH2501",
                LocalDate.of(2026, 3, 16), LocalDate.of(2026, 9, 22),
                "Закончились товары"));


        VBox root = new VBox();
        HBox topButtonsBox = new HBox();
        HBox bottomButtonsBox = new HBox();

        Button loadFromCsv = new Button("Загрузить из файла");
        Button saveToCsv = new Button("Сохранить в файл");


        Button editRecord = new Button("Изменить запись");
        editRecord.setDisable(true);

        Button addRecord = new Button("Добавить запись");

        TableView<StorageItem> recordsTable = new TableView<>();

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

        TableColumn<StorageItem, String> countryCol = new TableColumn("Страна-отправитель");
        countryCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ImportBatch importBatch) {
                return new SimpleStringProperty(importBatch.getCountry());
            }

            return new SimpleStringProperty("-");
        });

        TableColumn<StorageItem, String> customsCodeCol = new TableColumn("Таможенный код");
        customsCodeCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ImportBatch importBatch) {
                return new SimpleStringProperty(importBatch.getCustomsCode());
            }

            return new SimpleStringProperty("-");
        });

        TableColumn<StorageItem, LocalDate> archiveDateCol = new TableColumn("Дата архивации");
        archiveDateCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ArchiveBatch archiveBatch) {
                return new SimpleObjectProperty(archiveBatch.archiveDate());
            }

            return new SimpleObjectProperty("-");
        });

        TableColumn<StorageItem, String> archiveReasonCol = new TableColumn("Причина архивации");
        archiveReasonCol.setCellValueFactory(cellData -> {
            StorageItem item = cellData.getValue();
            if (item instanceof ArchiveBatch archiveBatch) {
                return new SimpleStringProperty(archiveBatch.archiveReason());
            }

            return new SimpleStringProperty("-");
        });

        recordsTable.getColumns().addAll(skuCol, nameCol, amountCol,
                cellCol, receiptDateCol, countryCol,
                customsCodeCol, archiveDateCol, archiveReasonCol);

        recordsTable.getItems().addAll(batches);
        recordsTable.getItems().addAll(importBatches);
        recordsTable.getItems().addAll(archiveBatches);


        topButtonsBox.getChildren().addAll(loadFromCsv, saveToCsv);
        topButtonsBox.setSpacing(10);

        bottomButtonsBox.getChildren().addAll(editRecord, addRecord);
        bottomButtonsBox.setSpacing(10);
        bottomButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        root.getChildren().addAll(topButtonsBox, recordsTable, bottomButtonsBox);
        root.setPadding(new Insets(10));
        root.setSpacing(10);



        recordsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        editRecord.setDisable(true);
                    } else {
                        editRecord.setDisable(!(newValue instanceof Editable));
                    }
                }
        );

        // TODO: написать setOnAction для каждой кнпки (для начала - тестовый)

        VBox.setVgrow(recordsTable, Priority.ALWAYS);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Storage App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
