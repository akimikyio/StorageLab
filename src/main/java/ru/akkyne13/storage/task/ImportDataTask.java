package ru.akkyne13.storage.task;

import javafx.concurrent.Task;
import ru.akkyne13.storage.io.Serializer;
import ru.akkyne13.storage.io.StorageData;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.structure.StorageMap;

import java.nio.file.Path;

public class ImportDataTask extends Task<StorageData> {
    private final Path file;
    private final Serializer<StorageData> serializer;
    private final StorageMap<String, StorageItem> storage;

    public ImportDataTask(Path file, StorageMap<String, StorageItem> storage, Serializer<StorageData> serializer) {
        this.file = file;
        this.storage = storage;
        this.serializer = serializer;
    }

    @Override
    protected StorageData call() throws Exception {
        updateMessage("Чтение файла...");
        updateProgress(-1, 100);

        StorageData data = serializer.load(file, (current, total) -> {
            updateProgress(current, total);
            updateMessage(String.format("Чтение: %d / %d", current, total));
        });

        if (isCancelled() || data == null) {
            return null;
        }

        updateMessage("Обновление структуры...");
        storage.clear();

        StorageItem[] items = data.getItems();
        for (int i = 0; i < items.length; i++) {
            if (isCancelled()) break;
            storage.put(items[i].getSku(), items[i]);

            if (i % 100 == 0 || i == items.length - 1) {
                updateProgress(i + 1, items.length);
            }
        }

        return data;
    }
}
