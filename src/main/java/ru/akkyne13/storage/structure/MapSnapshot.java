package ru.akkyne13.storage.structure;

public record MapSnapshot(String[] keys, Object[] values, boolean[] probes) {
    public static final String DELETED_MARKER = "__DELETED__";
}
