package ru.akkyne13.storage;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

// TODO: рефакторинг в MVC
public class MainView extends VBox {
    public MainView() {
        HBox topButtonsBox = new HBox();
        HBox bottomButtonsBox = new HBox();

        Button loadFromCsv = new Button("Загрузить из файла");
        Button saveToCsv = new Button("Сохранить в файл");

        Button editRecord = new Button("Изменить запись");
        Button addRecord = new Button("Добавить запись");


        topButtonsBox.getChildren().addAll(loadFromCsv, saveToCsv);
        bottomButtonsBox.getChildren().addAll(editRecord, addRecord);
        bottomButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        StorageTableView recordsTable = new StorageTableView();

        recordsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        editRecord.setDisable(true);
                    } else {
                        editRecord.setDisable(!(newValue instanceof Editable));
                    }
                }
        );

        loadFromCsv.setOnAction(event -> {
            try {
                List<StorageItem> items = CsvLoader.loadBatchesFromCsv("/home/Gleb/IdeaProjects/Storage/src/main/java/data.csv");
                recordsTable.getItems().setAll(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
            IO.println("Кликнули \"загрузить таблицу\"");
        });

        saveToCsv.setOnAction(event -> {
            IO.println("Кликнули \"сохранить таблицу\"");
        });

        editRecord.setOnAction(event -> {
            IO.println("Кликнули \"Редактировать запись\"");
        });
        addRecord.setOnAction(event -> {
            BatchAddDialog batchAddDialog = new BatchAddDialog();
            StorageItem createdRecord = batchAddDialog.showDialog();

            if (createdRecord != null) {
                recordsTable.getItems().add(createdRecord);
            }
        });

        this.setPadding(new Insets(10));
        this.setSpacing(10);
        VBox.setVgrow(recordsTable, Priority.ALWAYS);

        this.getChildren().addAll(topButtonsBox, recordsTable, bottomButtonsBox);
    }
}
