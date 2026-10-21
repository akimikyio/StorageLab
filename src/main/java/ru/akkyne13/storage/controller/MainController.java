package ru.akkyne13.storage.controller;

import javafx.scene.control.Button;
import ru.akkyne13.storage.structure.OpenAddressingHashMap;
import ru.akkyne13.storage.structure.StorageMap;
import ru.akkyne13.storage.util.AlertUtil;
import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.util.CsvLoadResult;
import ru.akkyne13.storage.util.CsvStorageUtil;
import ru.akkyne13.storage.model.Editable;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.view.MainView;
import ru.akkyne13.storage.view.StorageTableView;

import java.io.IOException;

public class MainController {
    private final MainView view;
    private final StorageMap<String, StorageItem> storage = new OpenAddressingHashMap<>();

    public MainController(MainView view) {
        this.view = view;
        setupListeners();
    }

    private void refreshTable() {
        Object[] values = storage.getValuesSnapshot();
        StorageItem[] items = new StorageItem[values.length];

        for (int i = 0; i < values.length; i++) {
            items[i] = (StorageItem) values[i];
        }

        view.getRecordsTable().getItems().setAll(items);
        view.getHashCanvas().drawHashTable(storage.getKeysSnapshot(), storage.getLastProbes());
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
                    if (!selectedItem.getSku().equals(updatedRecord.getSku())) {
                        storage.delete(selectedItem.getSku());
                    }
                    storage.put(updatedRecord.getSku(), updatedRecord);

                    recordsTable.getItems().set(selectedIndex, updatedRecord);

                    view.getHashCanvas().drawHashTable(storage.getKeysSnapshot(), storage.getLastProbes());
                }
            }
        });
    }

    private void setupAddRecordButton() {
        Button addRecordButton = view.getAddRecordButton();

        addRecordButton.setOnAction(event -> {
            BatchFormController batchFormController = new BatchFormController();
            StorageItem createdRecord = batchFormController.showDialog();

            if (createdRecord != null) {
                storage.put(createdRecord.getSku(), createdRecord);
                refreshTable();
            }
        });
    }

    private void setupLoadFromCsvButton() {
        Button loadFromCsvButton = view.getLoadFromCsvButton();

        loadFromCsvButton.setOnAction(event -> {
            try {
                CsvLoadResult csvLoadResult = CsvStorageUtil.loadBatchesFromCsv("/home/Gleb/IdeaProjects/Storage/src/main/java/data.csv");
                storage.clear();

                for (StorageItem item : csvLoadResult.validItems()) {
                    storage.put(item.getSku(), item);
                }

                refreshTable();

                if (csvLoadResult.errors().length > 0) {
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

        saveToCsvButton.setOnAction(event -> {
            Object[] values = storage.getValuesSnapshot();
            StorageItem[] items = new StorageItem[values.length];

            for (int i = 0; i < values.length; i++) {
                items[i] = (StorageItem) values[i];
            }

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
