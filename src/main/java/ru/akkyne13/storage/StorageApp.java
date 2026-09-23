package ru.akkyne13.storage;

import javafx.application.Application;
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

        VBox root = new VBox();
        HBox buttonsBox = new HBox();

        Button loadFromCsv = new Button("Загрузить из файла");
        Button saveToCsv = new Button("Сохранить в файл");
        Button editRecord = new Button("Изменить запись");
        Button addRecord = new Button("Добавить запись");

        TableView<StorageItem> recordsTable = new TableView<>();

        TableColumn<StorageItem, String> skuCol = new TableColumn<>("Артикул");
        skuCol.setCellValueFactory(new PropertyValueFactory<>("sku"));

        TableColumn<StorageItem, String> nameCol = new TableColumn<>("Наименование");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<StorageItem, String> amountCol = new TableColumn<>("Количество");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<StorageItem, String> cellCol = new TableColumn<>("Ячейка");
        cellCol.setCellValueFactory(new PropertyValueFactory<>("cell"));

        TableColumn<StorageItem, String> receiptDateCol = new TableColumn<>("Дата поступления");
        receiptDateCol.setCellValueFactory(new PropertyValueFactory<>("receiptDate"));

        recordsTable.getColumns().addAll(skuCol, nameCol, amountCol, cellCol, receiptDateCol);

        buttonsBox.getChildren().addAll(loadFromCsv, saveToCsv, editRecord, addRecord);
        buttonsBox.setSpacing(10);

        root.getChildren().addAll(buttonsBox, recordsTable);
        root.setPadding(new Insets(10));
        root.setSpacing(10);

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
