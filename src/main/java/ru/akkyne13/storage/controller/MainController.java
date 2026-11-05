package ru.akkyne13.storage.controller;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import ru.akkyne13.storage.structure.MapSnapshot;
import ru.akkyne13.storage.structure.OpenAddressingHashMap;
import ru.akkyne13.storage.structure.StorageMap;
import ru.akkyne13.storage.task.GenerateDataTask;
import ru.akkyne13.storage.task.LoadCsvTask;
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
        MapSnapshot snapshot = storage.getSnapshot();
        Object[] values = snapshot.values();
        StorageItem[] items = new StorageItem[values.length];

        for (int i = 0; i < values.length; i++) {
            items[i] = (StorageItem) values[i];
        }

        view.getRecordsTable().getItems().setAll(items);
        view.getHashCanvas().drawHashTable(snapshot.keys(), snapshot.probes());
    }

    private void setupEditRecordsButtonState() {
        StorageTableView recordsTable = view.getRecordsTable();
        Button editRecordButton = view.getEditRecordButton();
        Button deleteRecordButton = view.getDeleteRecordButton();

        recordsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        editRecordButton.setDisable(true);
                        deleteRecordButton.setDisable(true);
                    } else {
                        editRecordButton.setDisable(!(newValue instanceof Editable));
                        deleteRecordButton.setDisable(false);
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

                    MapSnapshot snapshot = storage.getSnapshot();
                    view.getHashCanvas().drawHashTable(snapshot.keys(), snapshot.probes());
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

    private void setupDeleteRecordButton() {
        Button deleteRecordButton = view.getDeleteRecordButton();
        StorageTableView recordsTable = view.getRecordsTable();

        deleteRecordButton.setOnAction(event -> {
            StorageItem selectedItem = recordsTable.getSelectionModel().getSelectedItem();

            if (selectedItem != null) {
                storage.delete(selectedItem.getSku());
                refreshTable();
            }
        });
    }

    private void setupLoadFromCsvButton() {
        Button loadFromCsvButton = view.getLoadFromCsvButton();

        loadFromCsvButton.setOnAction(event -> {
            LoadCsvTask task = new LoadCsvTask("/home/Gleb/IdeaProjects/Storage/src/main/java/data.csv", storage);
            bindTaskToUI(task);

            task.addEventHandler(javafx.concurrent.WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
                CsvLoadResult result = task.getValue();
                if (result != null && result.errors().length > 0) {
                    StringBuilder warningMessage = new StringBuilder();
                    warningMessage.append("Файл загружен частично. Пропущены битые строки:\n");
                    for (CsvParseException error : result.errors()) {
                        warningMessage.append(error.getMessage()).append("\n");
                    }
                    AlertUtil.showWarning("Ошибки чтения", warningMessage.toString());
                }
            });

            Thread backgroundThread = new Thread(task);
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        });
    }

    private void setupSaveToCsvButton() {
        Button saveToCsvButton = view.getSaveToCsvButton();

        saveToCsvButton.setOnAction(event -> {
            Object[] values = storage.getSnapshot().values();
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

    private void bindTaskToUI(Task<?> task) {
        view.getGenerateButton().setDisable(true);
        view.getLoadFromCsvButton().setDisable(true);
        view.getCancelTaskButton().setDisable(false);

        view.getProgressBar().progressProperty().bind(task.progressProperty());

        view.getCancelTaskButton().setOnAction(e -> task.cancel());

        task.setOnSucceeded(e -> cleanupTaskUI());
        task.setOnFailed(e -> {
            cleanupTaskUI();
            AlertUtil.showError("Сбой", "Произошла ошибка при выполнении фоновой задачи.");
        });
        task.setOnCancelled(e -> cleanupTaskUI());
    }

    private void cleanupTaskUI() {
        view.getGenerateButton().setDisable(false);
        view.getLoadFromCsvButton().setDisable(false);
        view.getCancelTaskButton().setDisable(true);
        view.getProgressBar().progressProperty().unbind();
        view.getProgressBar().setProgress(0);
        refreshTable();
    }

    private void setupGenerateButton() {
        Button generateButton = view.getGenerateButton();

        generateButton.setOnAction(event -> {
            int count;
            try {
                count = Integer.parseInt(view.getGenerateCountField().getText());
            } catch (NumberFormatException e) {
                AlertUtil.showError("Ошибка", "Введите корректное число для генерации");
                return;
            }

            boolean delay = view.getDelayCheckBox().isSelected();
            boolean animate = view.getAnimateCheckBox().isSelected();

            generateButton.setDisable(true);

            GenerateDataTask task = new GenerateDataTask(count, storage, delay, animate, this::refreshTable);
            bindTaskToUI(task);

            Thread backgroundThread = new Thread(task);
            backgroundThread.setDaemon(true);
            backgroundThread.start();
        });
    }


    private void setupListeners() {
        setupEditRecordsButtonState();
        setupEditRecordButton();
        setupAddRecordButton();
        setupDeleteRecordButton();
        setupLoadFromCsvButton();
        setupSaveToCsvButton();
        setupGenerateButton();
    }
}
