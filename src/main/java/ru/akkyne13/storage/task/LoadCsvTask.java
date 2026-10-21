package ru.akkyne13.storage.task;

import javafx.concurrent.Task;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.structure.StorageMap;
import ru.akkyne13.storage.util.CsvLoadResult;
import ru.akkyne13.storage.util.CsvStorageUtil;

public class LoadCsvTask extends Task<CsvLoadResult> {
    private final String filePath;
    private final StorageMap<String, StorageItem> storage;

    public LoadCsvTask(String filePath, StorageMap<String, StorageItem> storage) {
        this.filePath = filePath;
        this.storage = storage;
    }

    @Override
    protected CsvLoadResult call() throws Exception {
        updateMessage("Чтение файла...");

        CsvLoadResult result = CsvStorageUtil.loadBatchesFromCsv(filePath, (current, total) -> {
            updateProgress(current, total);
            updateMessage(String.format("Чтение: %d / %d", current, total));
        });

        if (isCancelled()) {
            return null;
        }

        updateMessage("Обновление структуры...");
        storage.clear();

        StorageItem[] items = result.validItems();
        for (int i = 0; i < items.length; i++) {
            if (isCancelled()) {
                break;
            }
            storage.put(items[i].getSku(), items[i]);

            if (i % 100 == 0 || i == items.length - 1) {
                updateProgress(i + 1, items.length);
            }
        }

        return result;
    }
}
