package ru.akkyne13.storage.controller;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import ru.akkyne13.storage.io.*;
import ru.akkyne13.storage.structure.MapSnapshot;
import ru.akkyne13.storage.structure.OpenAddressingHashMap;
import ru.akkyne13.storage.structure.StorageMap;
import ru.akkyne13.storage.task.GenerateDataTask;
import ru.akkyne13.storage.task.ImportDataTask;
import ru.akkyne13.storage.util.AlertUtil;
import ru.akkyne13.storage.model.Editable;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.view.MainView;
import ru.akkyne13.storage.view.StorageTableView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainController {
    private final MainView view;
    private final StorageMap<String, StorageItem> storage = new OpenAddressingHashMap<>();
    private final Map<String, Serializer<StorageData>> serializers = new HashMap<>();


    public MainController(MainView view) {
        this.view = view;

        serializers.put("json", new JsonSerializer());
        serializers.put("bin", new BinSerializer());
        serializers.put("csv", new CsvSerializer());

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

    private void setupImportButon() {
        Button importButton = view.getImportButton();
        FileChooser fileChooser = setupFileChooser();

        importButton.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(view.getScene().getWindow());
            if (file != null) {
                String extension = getExtension(file);

                Serializer<StorageData> serializer = serializers.get(extension);

                if (serializer == null) {
                    AlertUtil.showError("Ошибка импорта", "Формат ." + extension + " не поддерживается.");
                    return;
                }

                ImportDataTask task = new ImportDataTask(file.toPath(), storage, serializer);
                bindTaskToUI(task);

                task.addEventHandler(javafx.concurrent.WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
                    StorageData result = task.getValue();

                    if (result != null && result.getErrors() != null && result.getErrors().length > 0) {
                        StringBuilder warningMessage = new StringBuilder("Файл загружен частично. Пропущены битые строки:\n");
                        for (var error : result.getErrors()) {
                            warningMessage.append(error.getMessage()).append("\n");
                        }
                        AlertUtil.showWarning("Ошибки чтения", warningMessage.toString());
                    } else {
                        AlertUtil.showInfo("Загрузка", "Данные успешно импортированы!");
                    }
                });

                Thread backgroundThread = new Thread(task);
                backgroundThread.setDaemon(true);
                backgroundThread.start();
            }
        });
    }

    private FileChooser setupFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберете файл");

        FileChooser.ExtensionFilter jsonFilter =
                new FileChooser.ExtensionFilter("JSON", "*.json");
        FileChooser.ExtensionFilter binFilter =
                new FileChooser.ExtensionFilter("Binary", "*.bin");
        FileChooser.ExtensionFilter csvFilter =
                new FileChooser.ExtensionFilter("CSV", "*.csv");
        FileChooser.ExtensionFilter allFilesFilter =
                new FileChooser.ExtensionFilter("Все файлы", "*.*");


        fileChooser.getExtensionFilters().addAll(jsonFilter, binFilter, csvFilter, allFilesFilter);

        return fileChooser;
    }

    private String getExtension(File file) {
        String fileName = file.getName();
        String extension = "";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex + 1).toLowerCase();
        }

        return extension;
    }


    private void setupExportButton() {
        Button saveToCsvButton = view.getExportButton();
        FileChooser fileChooser = setupFileChooser();


        saveToCsvButton.setOnAction(event -> {
            File file = fileChooser.showSaveDialog(view.getScene().getWindow());

            if (file != null) {
                Object[] values = storage.getSnapshot().values();
                StorageItem[] items = new StorageItem[values.length];

                for (int i = 0; i < values.length; i++) {
                    items[i] = (StorageItem) values[i];
                }

                StorageData data = new StorageData(1, items);
                String extension = getExtension(file);

                Serializer<StorageData> serializer = serializers.get(extension);

                if (serializer == null) {
                    AlertUtil.showError("Ошибка экспорта", "Формат ." + extension + " не поддерживается.");
                    return;
                }

                try {
                    serializer.save(data, file.toPath());
                    AlertUtil.showInfo("Сохранение данных","Файл успешно сохранён");
                } catch (IOException e) {
                    AlertUtil.showError("Ошибка сохранения файла", "Не удалось сохранить файл: " + e.getMessage());
                }

            }
        });
    }

    private void bindTaskToUI(Task<?> task) {
        view.getGenerateButton().setDisable(true);
        view.getImportButton().setDisable(true);
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
        view.getImportButton().setDisable(false);
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
        setupImportButon();
        setupExportButton();
        setupGenerateButton();
    }
}
