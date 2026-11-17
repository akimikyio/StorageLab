package ru.akkyne13.storage.migration;

public record RawStorageData(
        int schemaVersion,
        RawStorageRecord[] records
) { }
