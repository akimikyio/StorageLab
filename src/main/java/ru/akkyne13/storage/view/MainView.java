package ru.akkyne13.storage.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainView extends VBox {
    private final Button loadFromCsvButton = new Button("Загрузить из файла");
    private final Button saveToCsvButton = new Button("Сохранить в файл");
    private final Button editRecordButton = new Button("Изменить запись");
    private final Button addRecordButton = new Button("Добавить запись");
    private final StorageTableView recordsTable = new StorageTableView();

    public MainView() {
        HBox topButtonsBox = new HBox();
        HBox bottomButtonsBox = new HBox();

        topButtonsBox.getChildren().addAll(loadFromCsvButton, saveToCsvButton);
        bottomButtonsBox.getChildren().addAll(editRecordButton, addRecordButton);
        bottomButtonsBox.setAlignment(Pos.CENTER_RIGHT);

        this.setPadding( new Insets(10));
        this.setSpacing(10);
        VBox.setVgrow(recordsTable, Priority.ALWAYS);

        this.getChildren().addAll(topButtonsBox, recordsTable, bottomButtonsBox);
    }

    public Button getLoadFromCsvButton() {
        return loadFromCsvButton;
    }

    public Button getSaveToCsvButton() {
        return saveToCsvButton;
    }

    public Button getEditRecordButton() {
        return editRecordButton;
    }

    public Button getAddRecordButton() {
        return addRecordButton;
    }

    public StorageTableView getRecordsTable() {
        return recordsTable;
    }
}
