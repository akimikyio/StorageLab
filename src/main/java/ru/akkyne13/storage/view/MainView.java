package ru.akkyne13.storage.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ru.akkyne13.storage.view.component.HashTableView;

public class MainView extends VBox {
    private final Button loadFromCsvButton = new Button("Загрузить из файла");
    private final Button saveToCsvButton = new Button("Сохранить в файл");
    private final Button editRecordButton = new Button("Изменить запись");
    private final Button addRecordButton = new Button("Добавить запись");
    private final StorageTableView recordsTable = new StorageTableView();
    private final HashTableView hashCanvas = new HashTableView();

    HBox createTopButtonsBox() {
        return new HBox(loadFromCsvButton, saveToCsvButton);
    }

    HBox createBottomButtonsBox() {
        HBox bottomButtonsBox = new HBox(editRecordButton, addRecordButton);
        bottomButtonsBox.setAlignment(Pos.CENTER_RIGHT);
        return bottomButtonsBox;
    }

    public MainView() {
        HBox topButtonsBox = createTopButtonsBox();
        HBox bottomButtonsBox = createBottomButtonsBox();

        this.setPadding( new Insets(10));
        this.setSpacing(10);
        VBox.setVgrow(recordsTable, Priority.ALWAYS);

        ScrollPane canvasScrollPane = new ScrollPane(hashCanvas); // Чтобы канвас можно было скроллить
        canvasScrollPane.setPrefHeight(200);

        this.getChildren().addAll(topButtonsBox, recordsTable, bottomButtonsBox, canvasScrollPane);
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

    public HashTableView getHashCanvas() { return hashCanvas; }
}
