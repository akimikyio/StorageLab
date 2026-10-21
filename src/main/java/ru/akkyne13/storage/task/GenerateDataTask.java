package ru.akkyne13.storage.task;

import javafx.concurrent.Task;
import ru.akkyne13.storage.model.StorageItem;
import ru.akkyne13.storage.structure.StorageMap;


public class GenerateDataTask extends Task<Void> {
    private final int recordsToGenerate;
    private final StorageMap<String, StorageItem> storage;
    private final boolean isDelayEnabled;


    public GenerateDataTask(int recordsToGenerate, StorageMap<String, StorageItem> storage) {
        this.recordsToGenerate = recordsToGenerate;
        this.storage = storage;
        this.isDelayEnabled = false;
    }

    public GenerateDataTask(int recordsToGenerate, StorageMap<String, StorageItem> storage, boolean isDelayEnabled) {
        this.recordsToGenerate = recordsToGenerate;
        this.storage = storage;
        this.isDelayEnabled = isDelayEnabled;
    }

    @Override
    protected Void call() throws Exception {
        long timestamp = System.currentTimeMillis() % 100000;
        int updateStep = Math.max(1, recordsToGenerate / 100);

        for (int i = 0; i < recordsToGenerate; i++) {
            if (isCancelled()) {
                updateMessage("Генерация прервана пользователем");
                break;
            }

            String sku = String.format("SKU-%d-%d", timestamp, i);
            StorageItem item = StorageItemFactory.createRandomItem(i, sku);

            storage.put(sku, item);
            if (i % updateStep == 0 || i == recordsToGenerate - 1) {
                updateProgress(i + 1, recordsToGenerate);
                updateMessage(String.format("Сгенерировано %d из %d", i + 1, recordsToGenerate));
            }

            if (isDelayEnabled) {
                Thread.sleep(5);
            }
        }
        return null;
    }
}
