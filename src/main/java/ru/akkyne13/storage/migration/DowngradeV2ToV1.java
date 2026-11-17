package ru.akkyne13.storage.migration;

public class DowngradeV2ToV1 implements SchemaStep {
    @Override
    public int fromVersion() {
        return 2;
    }

    @Override
    public int toVersion() {
        return 1;
    }

    @Override
    public RawStorageRecord apply(RawStorageRecord record) {
        RawStorageRecord copy = record.copy();
        copy.remove("note");
        return copy;
    }
}
