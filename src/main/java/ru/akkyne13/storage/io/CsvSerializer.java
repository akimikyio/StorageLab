package ru.akkyne13.storage.io;

import ru.akkyne13.storage.util.CsvLoadResult;
import ru.akkyne13.storage.util.CsvStorageUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiConsumer;

public class CsvSerializer implements Serializer<StorageData> {
    @Override
    public String format() {
        return "csv";
    }

    @Override
    public void save(StorageData data, Path file) throws IOException {
        CsvStorageUtil.saveBatchesToCsv(data.getItems(), file.toString());
    }

    @Override
    public StorageData load(Path file) throws IOException {
        return load(file, null);
    }

    @Override
    public StorageData load(Path file, BiConsumer<Integer, Integer> progressCallback) throws IOException {
        CsvLoadResult result = CsvStorageUtil.loadBatchesFromCsv(file.toString(), progressCallback);
        return new StorageData(1, result.validItems(), result.errors());
    }
}
