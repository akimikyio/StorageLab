package ru.akkyne13.storage.util;

import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.model.StorageItem;


public record CsvLoadResult(
        StorageItem[] validItems,
        CsvParseException[] errors
) { }
