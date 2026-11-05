package ru.akkyne13.storage.util;

import ru.akkyne13.storage.model.StorageItem;

public interface CsvRowMapper<T extends StorageItem>  {
    String map(T item);
}
