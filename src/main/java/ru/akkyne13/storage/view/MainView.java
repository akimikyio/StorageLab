package ru.akkyne13.storage.view;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ru.akkyne13.storage.view.component.HashTableView;

public class MainView extends VBox {
    private final Button loadFromCsvButton = new Button("Загрузить из файла");
    private final Button saveToCsvButton = new Button("Сохранить в файл");

    private final Button editRecordButton = new Button("Изменить запись");
    private final Button addRecordButton = new Button("Добавить запись");
    private final Button deleteRecordButton = new Button("Удалить запись");

    private final StorageTableView recordsTable = new StorageTableView();
    private final HashTableView hashCanvas = new HashTableView();

    private final TextField generateCountField = new TextField("100");
    private final Button generateButton = new Button("Сгенерировать записи");
    private final Button cancelTaskButton = new Button("Отмена");
    private final CheckBox delayCheckBox = new CheckBox("Задержка");
    private final CheckBox animateCheckBox = new CheckBox("Анимация");
    private final ProgressBar progressBar = new ProgressBar(0);

    HBox createGenerateBox() {
        generateCountField.setPrefWidth(60);
        progressBar.setPrefWidth(150);
        cancelTaskButton.setDisable(true);

        VBox checksBox = new VBox(2, delayCheckBox, animateCheckBox);
        HBox generateBox = new HBox(10, generateButton, generateCountField, cancelTaskButton, progressBar, checksBox);
        generateBox.setAlignment(Pos.CENTER_LEFT);

        return generateBox;
    }

    HBox createEditAddDeleteBox() {
        HBox editAddBox = new HBox(10, editRecordButton, deleteRecordButton, addRecordButton);
        editAddBox.setAlignment(Pos.CENTER_RIGHT);

        return editAddBox;
    }

    HBox createTopButtonsBox() {
        return new HBox(loadFromCsvButton, saveToCsvButton);
    }

    HBox createBottomButtonsBox() {
        HBox generateBox = createGenerateBox();
        HBox editAddBox = createEditAddDeleteBox();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomButtonsBox = new HBox(generateBox, spacer, editAddBox);
        bottomButtonsBox.setAlignment(Pos.CENTER);

        return bottomButtonsBox;
    }

    // TODO: вынести создание кэнваса в отдельный метод
    public MainView() {
        HBox topButtonsBox = createTopButtonsBox();
        HBox bottomButtonsBox = createBottomButtonsBox();

        this.setPadding( new Insets(10));
        this.setSpacing(10);
        VBox.setVgrow(recordsTable, Priority.ALWAYS);

        ScrollPane canvasScrollPane = new ScrollPane(hashCanvas);
        canvasScrollPane.setPrefHeight(200);

        TitledPane canvasTitledPane = new TitledPane("Визуализация хеш-таблицы", canvasScrollPane);
        canvasTitledPane.setExpanded(false);
        canvasTitledPane.setAnimated(true);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(recordsTable, canvasTitledPane);
        splitPane.setDividerPositions(0.6);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        this.getChildren().addAll(topButtonsBox, splitPane, bottomButtonsBox);
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

    public TextField getGenerateCountField() {
        return generateCountField;
    }

    public Button getGenerateButton() {
        return generateButton;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Button getDeleteRecordButton() {
        return deleteRecordButton;
    }

    public CheckBox getAnimateCheckBox() {
        return animateCheckBox;
    }

    public CheckBox getDelayCheckBox() {
        return delayCheckBox;
    }

    public Button getCancelTaskButton() {
        return cancelTaskButton;
    }
}
