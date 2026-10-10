package ru.akkyne13.storage.controller;

import javafx.scene.control.Button;
import ru.akkyne13.storage.util.AlertUtil;
import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.util.CsvLoadResult;
import ru.akkyne13.storage.util.CsvStorageUtil;
import ru.akkyne13.storage.model.Editable;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.view.MainView;
import ru.akkyne13.storage.view.StorageTableView;

import java.io.IOException;
import java.util.List;

public class MainController {
    private final MainView view;

    public MainController(MainView view) {
        this.view = view;
        setupListeners();
    }

    private void setupEditRecordsButtonState() {
        StorageTableView recordsTable = view.getRecordsTable();
        Button editRecordButton = view.getEditRecordButton();

        recordsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        editRecordButton.setDisable(true);
                    } else {
                        editRecordButton.setDisable(!(newValue instanceof Editable));
                    }
                }
        );
    }

    private void setupEditRecordButton() {
        Button editRecordButton = view.getEditRecordButton();
        StorageTableView recordsTable = view.getRecordsTable();

        editRecordButton.setOnAction(event -> {
            int selectedIndex = recordsTable.getSelectionModel().getSelectedIndex();
            StorageItem selectedItem = recordsTable.getSelectionModel().getSelectedItem();

            if (selectedIndex >= 0 && selectedItem != null) {
                BatchFormController editDialog = new BatchFormController(selectedItem);
                StorageItem updatedRecord = editDialog.showDialog();

                if (updatedRecord != null) {
                    recordsTable.getItems().set(selectedIndex, updatedRecord);
                }
            }
        });
    }

    private void setupAddRecordButton() {
        Button addRecordButton = view.getAddRecordButton();
        StorageTableView recordsTable = view.getRecordsTable();

        addRecordButton.setOnAction(event -> {
            BatchFormController batchFormController = new BatchFormController();
            StorageItem createdRecord = batchFormController.showDialog();

            if (createdRecord != null) {
                recordsTable.getItems().add(createdRecord);
            }
        });
    }

    private void setupLoadFromCsvButton() {
        Button loadFromCsvButton = view.getLoadFromCsvButton();
        StorageTableView recordsTable = view.getRecordsTable();

        loadFromCsvButton.setOnAction(event -> {
            try {
                CsvLoadResult csvLoadResult = CsvStorageUtil.loadBatchesFromCsv("/home/Gleb/IdeaProjects/Storage/src/main/java/data.csv");
                recordsTable.getItems().setAll(csvLoadResult.validItems());

                if (!csvLoadResult.errors().isEmpty()) {
                    StringBuilder warningMessage = new StringBuilder();
                    warningMessage.append("Файл загружен частично. Пропущены битые строки:\n");
                    for (CsvParseException error : csvLoadResult.errors()) {
                        warningMessage.append(error.getMessage()).append("\n");
                    }

                    AlertUtil.showWarning("Ошибки чтения", warningMessage.toString());
                }

            } catch (IOException e) {
                AlertUtil.showError("Системная ошибка", "Не удалось открыть файл");
            }
        });
    }

    private void setupSaveToCsvButton() {
        Button saveToCsvButton = view.getSaveToCsvButton();
        StorageTableView recordsTable = view.getRecordsTable();

        saveToCsvButton.setOnAction(event -> {
            List<StorageItem> items = recordsTable.getItems();
            try {
                CsvStorageUtil.saveBatchesToCsv(items, "/home/Gleb/IdeaProjects/Storage/src/main/java/data.csv");
                AlertUtil.showInfo("Сохранение данных","Файл успешно сохранён");
            } catch (IOException e) {
                AlertUtil.showError("Ошибка сохранения файла", "Не удалось сохранить файл");
            }
        });
    }


    private void setupListeners() {
        setupEditRecordsButtonState();
        setupEditRecordButton();
        setupAddRecordButton();
        setupLoadFromCsvButton();
        setupSaveToCsvButton();
    }
}
