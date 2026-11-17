package ru.akkyne13.storage.migration;

import ru.akkyne13.storage.exception.SchemaMigrationException;

public interface SchemaStep {
    int fromVersion();
    int toVersion();
    RawStorageRecord apply(RawStorageRecord record) throws SchemaMigrationException;
}
