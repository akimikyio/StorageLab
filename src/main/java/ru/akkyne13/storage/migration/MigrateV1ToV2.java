package ru.akkyne13.storage.migration;

import ru.akkyne13.storage.exception.SchemaMigrationException;

public class MigrateV1ToV2 implements SchemaStep {

    private static final String[] REQUIRED_V1_FIELDS = {
            "type", "sku", "name", "amount", "cell", "receiptDate"
    };

    @Override
    public int fromVersion() {
        return 1;
    }

    @Override
    public int toVersion() {
        return 2;
    }

    @Override
    public RawStorageRecord apply(RawStorageRecord record) throws SchemaMigrationException {
        RawStorageRecord copy = record.copy();

        for (String field : REQUIRED_V1_FIELDS) {
            copy.getRequiredNonBlank(field);
        }

        copy.putIfAbsent("note", "");

        return copy;
    }
}
