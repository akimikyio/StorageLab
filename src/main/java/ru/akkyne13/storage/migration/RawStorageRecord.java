package ru.akkyne13.storage.migration;

import ru.akkyne13.storage.exception.SchemaMigrationException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RawStorageRecord {
    private final Map<String, String> fields = new LinkedHashMap<>();

    public RawStorageRecord() {
    }

    public RawStorageRecord copy() {
        RawStorageRecord copy = new RawStorageRecord();
        copy.fields.putAll(this.fields);
        return copy;
    }

    public void put(String key, String value) {
        Objects.requireNonNull(key, "Имя поля не может быть null");
        if (value != null) {
            fields.put(key, value);
        }
    }

    public void putIfAbsent(String key, String defaultValue) {
        Objects.requireNonNull(key, "Имя поля не может быть null");
        if (defaultValue != null) {
            fields.putIfAbsent(key, defaultValue);
        }
    }

    public void remove(String key) {
        fields.remove(key);
    }

    public boolean containsKey(String key) {
        return fields.containsKey(key);
    }

    public String getRequired(String key) throws SchemaMigrationException {
        String value = fields.get(key);
        if (value == null) {
            throw new SchemaMigrationException(
                    SchemaMigrationException.SchemaMigrationErrorCode.MISSING_FIELD,
                    "отсутствует поле '" + key + "'"
            );
        }
        return value;
    }

    public String getRequiredNonBlank(String key) throws SchemaMigrationException {
        String value = getRequired(key);
        if (value.isBlank()) {
            throw new SchemaMigrationException(
                    SchemaMigrationException.SchemaMigrationErrorCode.INVALID_FIELD_VALUE,
                    "поле '" + key + "' не может быть пустым"
            );
        }
        return value;
    }

    public String getOptional(String key) {
        return fields.get(key);
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawStorageRecord that = (RawStorageRecord) o;
        return Objects.equals(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }

    @Override
    public String toString() {
        return "RawStorageRecord" + fields;
    }
}
