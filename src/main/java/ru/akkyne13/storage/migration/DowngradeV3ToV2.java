package ru.akkyne13.storage.migration;

public class DowngradeV3ToV2 implements SchemaStep {

    @Override
    public int fromVersion() {
        return 3;
    }

    @Override
    public int toVersion() {
        return 2;
    }

    @Override
    public RawStorageRecord apply(RawStorageRecord record) {
        RawStorageRecord copy = record.copy();
        copy.remove("updatedAt");
        return copy;
    }
}
