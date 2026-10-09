package ru.akkyne13.storage.util;

import ru.akkyne13.storage.exception.CsvParseException;
import ru.akkyne13.storage.model.StorageItem;

import java.util.List;

public record CsvLoadResult(
        List<StorageItem> validItems,
        List<CsvParseException> errors
) { }
